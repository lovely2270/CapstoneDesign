package kjharu.com.capstone_2020

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.android.synthetic.main.emp_add.*
import android.widget.RadioGroup

class EmpFragment(var userId : String?) : Fragment() {

    val firebaseReference: FirebaseDatabase = FirebaseDatabase.getInstance()
    val databaseUser = firebaseReference.reference.child("user")
    var empList = ArrayList<Emp>()
    var count : Int = 0
    //var empRadioType : String = "직원"

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
                //직원 경로
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
                    val empType = dialogView.findViewById<RadioGroup>(R.id.radio_group)
                   // empType.setText(typeNow.toString())
                  /*  val empTypeJ = dialogView.findViewById<RadioButton>(R.id.radio_j)
                    empType.setText(typeNow.toString())
                    val empTypeA = dialogView.findViewById<RadioButton>(R.id.radio_a)
                    empType.setText(typeNow.toString())*/

                    /*
                    empType.setOnCheckedChangeListener { group, checkedId ->
                        if (group.id == R.id.radio_group) {
                            if (checkedId == R.id.radio_j) {
                                if (radio_j.isChecked == true) {
                                    radio_j.isChecked = true
                                    radio_a.isChecked = false
                                    empRadioType = radio_j.text.toString()
                                }
                            } else if (checkedId == R.id.radio_a) {
                                if (radio_a.isChecked == true) {
                                    radio_j.isChecked = false
                                    radio_a.isChecked = true
                                    empRadioType = radio_a.text.toString()
                                }
                            }
                        }
                    }*/



                    builder.setNegativeButton("취소", null)
                    builder.setPositiveButton("확인") { dialogInterface, i ->
                        var empRadioType : String = ""
                        if(empType.checkedRadioButtonId == R.id.radio_j){
                            empRadioType = "직원"
                        }else {
                            empRadioType = "알바"
                        }

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
                            .setValue(empRadioType)
                       /* databaseUser.child(userId.toString()).child(datkey).child("emp")
                            .child("e" + position).child("empType")
                            .setValue(empTypeJ.text.toString())
                        databaseUser.child(userId.toString()).child(datkey).child("emp")
                            .child("e" + position).child("empType")
                            .setValue(empTypeA.text.toString())*/
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

        //직원 추가 버튼 클릭
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
                    val empType = dialogView.findViewById<RadioGroup>(R.id.radio_group)

                   /* var memptype: RadioGroup.OnCheckedChangeListener =
                        RadioGroup.OnCheckedChangeListener { group, checkedId ->
                            if (group.id == R.id.radio_group) {
                                if (checkedId == R.id.radio_j) {
                                    if (radio_j.isChecked == true) {
                                        radio_j.isChecked = true
                                        radio_a.isChecked = false
                                        empRadioType = radio_j.text.toString()
                                    }
                                } else if (checkedId == R.id.radio_a) {
                                    if (radio_a.isChecked == true) {
                                        radio_j.isChecked = false
                                        radio_a.isChecked = true
                                        empRadioType = radio_a.text.toString()
                                    }
                                }
                            }
                        }*/
                    /*
                    empType.setOnCheckedChangeListener { group, checkedId ->
                        if (group.id == R.id.radio_group) {
                            if (checkedId == R.id.radio_j) {
                                if (radio_j.isChecked == true) {
                                    radio_j.isChecked = true
                                    radio_a.isChecked = false
                                    empRadioType = radio_j.text.toString()
                                }
                            } else if (checkedId == R.id.radio_a) {
                                if (radio_a.isChecked == true) {
                                    radio_j.isChecked = false
                                    radio_a.isChecked = true
                                    empRadioType = radio_a.text.toString()
                                }
                            }
                        }
                    }*/

                            builder.setNegativeButton("취소", null)
                            builder.setPositiveButton("확인") { dialogInterface, i ->
                                var empRadioType : String = ""
                                if(empType.checkedRadioButtonId == R.id.radio_j){
                                    empRadioType = "직원"
                                }else {
                                    empRadioType = "알바"
                                }
                                //DB에 저장
                                databaseUser.child(userId.toString()).child(datkey).child("emp")
                                    .child("e" + count).child("empName")
                                    .setValue(empName.text.toString())
                                databaseUser.child(userId.toString()).child(datkey).child("emp")
                                    .child("e" + count).child("empSalary")
                                    .setValue(empSalary.text.toString())
                                databaseUser.child(userId.toString()).child(datkey).child("emp")
                                    .child("e" + count).child("empTime")
                                    .setValue(empTime.text.toString())
                                databaseUser.child(userId.toString()).child(datkey).child("emp")
                                .child("e" + count).child("empType")
                                .setValue(empRadioType)
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