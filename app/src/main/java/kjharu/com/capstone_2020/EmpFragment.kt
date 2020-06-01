package kjharu.com.capstone_2020

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.listview_emp.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EmpFragment(var userId : String?) : Fragment() {

    val firebaseReference: FirebaseDatabase = FirebaseDatabase.getInstance()
    val databaseUser = firebaseReference.reference.child("user")
    var empList = ArrayList<Emp>()
    var count : Int = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState)
        var inflaterview = inflater.inflate(R.layout.fragment_emp, container, false)

        //년도월 키 받아오기
        val current = LocalDate.now()
        val datkey = current.format(DateTimeFormatter.ofPattern("yyyyMM"))
        val databaseYM = databaseUser.child(userId.toString()).child(datkey)

        //val item = Array(20, {""})
        val empAdapter = empAdapter(context!!, empList, userId)
        val lv = inflaterview.findViewById(R.id.empListView) as ListView
        lv.setAdapter(empAdapter)

        //리스트 불러주기
        databaseUser.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                empList.clear()
                //재고 경로
                var databaseStock = p0.child(userId.toString()).child(datkey).child("emp")

                for (i in 0..databaseStock.childrenCount - 1) {
                    var emp: Emp = Emp("", "", "","")
                    emp.name = databaseStock.child("e" + i).child("empName").value.toString()
                    emp.salary = databaseStock.child("e" + i).child("empSalary").value.toString()
                    emp.time = databaseStock.child("e" + i).child("empTime").value.toString()
                    emp.type = databaseStock.child("e" + i).child("empType").value.toString()

                    empList.add(emp)

                    empAdapter.notifyDataSetChanged()
                    empAdapter.notifyDataSetInvalidated()
                }
                count = databaseStock.childrenCount.toInt()
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })

        //클릭리스너 (수정 시)
        lv.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            databaseUser.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val dialogView = inflater.inflate(R.layout.emp_add, null)

                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("직원 수정")
                    builder.setView(dialogView)

                    //현재 수정하려는 재고의 값들
                    //현재 직원명
                    var nameNow = p0.child(userId.toString()).child(datkey).child("emp")
                        .child("e" + position).child("empName").value
                    //현재 직원 월급
                    var salaryNow = p0.child(userId.toString()).child(datkey).child("emp")
                        .child("e" + position).child("empSalary").value
                    //현재 직원 근무 시간
                    var timeNow = p0.child(userId.toString()).child(datkey).child("emp")
                        .child("e" + position).child("empTime").value
                    //현재 직원 직급
                    var typeNow = p0.child(userId.toString()).child(datkey).child("emp")
                        .child("e" + position).child("empType").value

                    val empName = dialogView.findViewById<EditText>(R.id.empNameForAdd)
                    empName.setText(nameNow.toString())
                    val empSalary = dialogView.findViewById<EditText>(R.id.empSalaryForAdd)
                    empSalary.setText(salaryNow.toString())
                    val empTime = dialogView.findViewById<EditText>(R.id.empTimeForAdd)
                    empTime.setText(timeNow.toString())
                    val empType = dialogView.findViewById<EditText>(R.id.empTypeForAdd)
                    empType.setText(typeNow.toString())

                    builder.setNegativeButton("취소", null)
                    builder.setPositiveButton("확인") { dialogInterface, i ->

                        //DB에 저장
                        databaseUser.child(userId.toString()).child(datkey).child("emp")
                            .child("e" + position).child("empName")
                            .setValue(empName.text.toString())
                        databaseUser.child(userId.toString()).child(datkey).child("emp")
                            .child("e" + position).child("empSalary")
                            .setValue(empSalary.text.toString())
                        databaseUser.child(userId.toString()).child(datkey).child("emp")
                            .child("e" + position).child("empTime")
                            .setValue(empTime.text.toString())
                        databaseUser.child(userId.toString()).child(datkey).child("emp")
                            .child("e" + position).child("empType")
                            .setValue(empType.text.toString())
                    }
                    empAdapter.notifyDataSetChanged()
                    empAdapter.notifyDataSetInvalidated()

                    builder.show()
                }
                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                }
            })
        }

        //길게 누르면 (삭제)
        lv.setOnItemLongClickListener({ parent, view, position, id ->
            databaseUser.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val databaseEmp =
                        databaseUser.child(userId.toString()).child(datkey).child("emp")

                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("경고")
                    builder.setMessage("*현 직원을 삭제하시겠습니까? 모든 내용이 삭제됩니다.*")

                    builder.setNegativeButton("취소", null)
                    builder.setPositiveButton("확인") { dialogInterface, i ->

                        val databaseEmpForGet =
                            p0.child(userId.toString()).child(datkey).child("emp")
                        //하나씩 앞당겨주기
                        for (i in position..databaseEmpForGet.childrenCount - 1) {
                            //다음 이름 가져오기
                            var nextName = databaseEmpForGet.child("e" + (i + 1))
                                .child("empName").value.toString()
                            //앞당겨저장
                            databaseEmp.child("e" + i).child("empName").setValue(nextName)
                            //다음 월급 가져오기
                            var nextSalary = databaseEmpForGet.child("e" + (i + 1))
                                .child("empSalary").value.toString()
                            //앞당겨저장
                            databaseEmp.child("e" + i).child("empSalary").setValue(nextSalary)
                            //다음 재고량 가져오기
                            var nextTime = databaseEmpForGet.child("e" + (i + 1))
                                .child("empTime").value.toString()
                            //앞당겨저장
                            databaseEmp.child("e" + i).child("empTime").setValue(nextTime)
                            //다음 재고원가 가져오기
                            var nextType = databaseEmpForGet.child("e" + (i + 1))
                                .child("empType").value.toString()
                            //앞당겨저장
                            databaseEmp.child("e" + i).child("empType").setValue(nextType)
                        }
                        //맨 마지막꺼 없애기(앞당겨줬으니)
                        databaseUser.child(userId.toString()).child(datkey).child("emp")
                            .child("e" + (databaseEmpForGet.childrenCount - 1)).removeValue()
                    }
                    empAdapter.notifyDataSetChanged()
                    empAdapter.notifyDataSetInvalidated()

                    builder.show()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                }
            })
            true
        })

        //재고 추가 버튼 클릭
        var btn_addEmp: Button = inflaterview.findViewById(R.id.btn_addEmp)
        btn_addEmp.setOnClickListener {
            databaseUser.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val dialogView = inflater.inflate(R.layout.emp_add, null)

                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("직원 추가")
                    builder.setView(dialogView)

                    val empName = dialogView.findViewById<EditText>(R.id.empNameForAdd)
                    val empSalary = dialogView.findViewById<EditText>(R.id.empSalaryForAdd)
                    val empTime = dialogView.findViewById<EditText>(R.id.empTimeForAdd)
                    val empType = dialogView.findViewById<EditText>(R.id.empTypeForAdd)

                    builder.setNegativeButton("취소", null)
                    builder.setPositiveButton("확인") { dialogInterface, i ->

                        //DB에 저장
                        databaseUser.child(userId.toString()).child(datkey).child("emp")
                            .child("e" + count).child("empName")
                            .setValue(empName.text.toString())
                        databaseUser.child(userId.toString()).child(datkey).child("emp")
                            .child("e" + count).child("empSalary")
                            .setValue(empSalary.text.toString())
                        databaseUser.child(userId.toString()).child(datkey).child("emp")
                            .child("e" + count).child("empTime").setValue(empTime.text.toString())
                        databaseUser.child(userId.toString()).child(datkey).child("emp")
                            .child("e" + count).child("empType")
                            .setValue(empType.text.toString())
                    }
                    empAdapter.notifyDataSetChanged()
                    empAdapter.notifyDataSetInvalidated()

                    builder.show()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                }
            })

        }

        return inflaterview
    }
}