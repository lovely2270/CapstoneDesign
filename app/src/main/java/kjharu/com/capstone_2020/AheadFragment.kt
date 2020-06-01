package kjharu.com.capstone_2020

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.annotation.RequiresApi
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.listview_aheaditem.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * A simple [Fragment] subclass.
 */
class AheadFragment(var userId : String?) : Fragment() {

    val firebaseReference: FirebaseDatabase = FirebaseDatabase.getInstance()
    val databaseUser = firebaseReference.reference.child("user")
    var aheadStockList = ArrayList<aheadStock>()
    var count : Int = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState)
        var inflaterview = inflater.inflate(R.layout.fragment_ahead, container, false)

        //년도월 키 받아오기
        val current = LocalDate.now()
        val datkey =  current.format(DateTimeFormatter.ofPattern("yyyyMM"))
        val databaseYM = databaseUser.child(userId.toString()).child(datkey)

        //val item = Array(20, {""})
        val aheadAdapter = aheadStockAdapter(context!!,aheadStockList,userId)
        val lv = inflaterview.findViewById(R.id.aheadListView) as ListView
        lv.setAdapter(aheadAdapter)

        //리스트 불러주기
        databaseUser.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                aheadStockList.clear()
                //재고 경로
                var databaseStock = p0.child(userId.toString()).child(datkey).child("aheadStock")

                for (i in 0..databaseStock.childrenCount - 1) {
                    var aheadstock : aheadStock = aheadStock("","","", "")
                    aheadstock.date = databaseStock.child("as"+i).child("aheadStockDate").value.toString()
                    aheadstock.name = databaseStock.child("as"+i).child("aheadStockName").value.toString()
                    aheadstock.num = databaseStock.child("as"+i).child("aheadStockNum").value.toString()
                    aheadstock.price = databaseStock.child("as"+i).child("aheadStockPrice").value.toString()

                    aheadStockList.add(aheadstock)

                    aheadAdapter.notifyDataSetChanged()
                    aheadAdapter.notifyDataSetInvalidated()

                }
                count = databaseStock.childrenCount.toInt()
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })

        //클릭리스너 (수정 시)
        lv.onItemClickListener= AdapterView.OnItemClickListener { parent, view, position, id ->
            databaseUser.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val dialogView = inflater.inflate(R.layout.aheadstock_add, null)

                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("입고 수정")
                    builder.setView(dialogView)

                    //현재 수정하려는 재고의 값들
                    //현재 입고 날짜
                    var dateNow = p0.child(userId.toString()).child(datkey).child("aheadStock")
                        .child("as" + position).child("aheadStockDate").value
                    //현재 입고명
                    var nameNow = p0.child(userId.toString()).child(datkey).child("aheadStock")
                        .child("as" + position).child("aheadStockName").value
                    //현재 입고량
                    var numNow = p0.child(userId.toString()).child(datkey).child("aheadStock")
                        .child("as" + position).child("aheadStockNum").value
                    //현재 입고 원가
                    var priceNow = p0.child(userId.toString()).child(datkey).child("aheadStock")
                        .child("as" + position).child("aheadStockPrice").value

                    val aheadStockDate = dialogView.findViewById<EditText>(R.id.aheadStockDateForAdd)
                    aheadStockDate.setText(dateNow.toString())
                    val aheadStockName = dialogView.findViewById<EditText>(R.id.aheadStockNameForAdd)
                    aheadStockName.setText(nameNow.toString())
                    val aheadStockNum = dialogView.findViewById<EditText>(R.id.aheadStockNumForAdd)
                    aheadStockNum.setText(numNow.toString())
                    val aheadStockPrice = dialogView.findViewById<EditText>(R.id.aheadStockPriceForAdd)
                    aheadStockPrice.setText(priceNow.toString())

                    builder.setNegativeButton("취소", null)
                    builder.setPositiveButton("확인") { dialogInterface, i ->

                        //DB에 저장
                        databaseUser.child(userId.toString()).child(datkey).child("aheadStock")
                            .child("as" + position).child("aheadStockDate")
                            .setValue(aheadStockDate.text.toString())
                        databaseUser.child(userId.toString()).child(datkey).child("aheadStock")
                            .child("as" + position).child("aheadStockName")
                            .setValue(aheadStockName.text.toString())
                        databaseUser.child(userId.toString()).child(datkey).child("aheadStock")
                            .child("as" + position).child("aheadStockNum")
                            .setValue(aheadStockNum.text.toString())
                        databaseUser.child(userId.toString()).child(datkey).child("aheadStock")
                            .child("as" + position).child("aheadStockPrice")
                            .setValue(aheadStockPrice.text.toString())
                    }
                    aheadAdapter.notifyDataSetChanged()
                    aheadAdapter.notifyDataSetInvalidated()

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
                    val databaseAheadStock =
                        databaseUser.child(userId.toString()).child(datkey).child("aheadStock")

                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("경고")
                    builder.setMessage("*입고 품목을 삭제하시겠습니까? 모든 내용이 삭제됩니다.*")

                    builder.setNegativeButton("취소", null)
                    builder.setPositiveButton("확인") { dialogInterface, i ->

                        val databaseAheadStockForGet =
                            p0.child(userId.toString()).child(datkey).child("aheadStock")
                        //하나씩 앞당겨주기
                        for (i in position..databaseAheadStockForGet.childrenCount - 1) {
                            //다음 입고 날짜 가져오기
                            var nextDate = databaseAheadStockForGet.child("as" + (i + 1))
                                .child("aheadStockDate").value.toString()
                            //앞당겨저장
                            databaseAheadStock.child("as" + i).child("aheadStockDate").setValue(nextDate)
                            //다음 이름 가져오기
                            var nextName = databaseAheadStockForGet.child("as" + (i + 1))
                                .child("aheadStockName").value.toString()
                            //앞당겨저장
                            databaseAheadStock.child("as" + i).child("aheadStockName").setValue(nextName)
                            //다음 재고량 가져오기
                            var nextNum = databaseAheadStockForGet.child("as" + (i + 1))
                                .child("aheadStockNum").value.toString()
                            //앞당겨저장
                            databaseAheadStock.child("as" + i).child("aheadStockNum").setValue(nextNum)
                            //다음 재고원가 가져오기
                            var nextPrice = databaseAheadStockForGet.child("as" + (i + 1))
                                .child("aheadStockPrice").value.toString()
                            //앞당겨저장
                            databaseAheadStock.child("as" + i).child("aheadStockPrice").setValue(nextPrice)
                        }
                        //맨 마지막꺼 없애기(앞당겨줬으니)
                        databaseUser.child(userId.toString()).child(datkey).child("aheadStock")
                            .child("as" + (databaseAheadStockForGet.childrenCount - 1)).removeValue()
                    }
                    aheadAdapter.notifyDataSetChanged()
                    aheadAdapter.notifyDataSetInvalidated()

                    builder.show()
                }
                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                }
            })
            true
        })

        //입고 추가 버튼 클릭
        var btn_addAheadStock: Button = inflaterview.findViewById(R.id.btn_addAheadStock)
        btn_addAheadStock.setOnClickListener {
            databaseUser.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val dialogView = inflater.inflate(R.layout.aheadstock_add, null)

                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("입고 추가")
                    builder.setView(dialogView)

                    val aheadStockDate = dialogView.findViewById<EditText>(R.id.aheadStockDateForAdd)
                    val aheadStockName = dialogView.findViewById<EditText>(R.id.aheadStockNameForAdd)
                    val aheadStockNum = dialogView.findViewById<EditText>(R.id.aheadStockNumForAdd)
                    val aheadStockPrice = dialogView.findViewById<EditText>(R.id.aheadStockPriceForAdd)

                    builder.setNegativeButton("취소", null)
                    builder.setPositiveButton("확인") { dialogInterface, i ->

                        //DB에 저장
                        databaseUser.child(userId.toString()).child(datkey).child("aheadStock")
                            .child("as" + count).child("aheadStockDate")
                            .setValue(aheadStockDate.text.toString())
                        databaseUser.child(userId.toString()).child(datkey).child("aheadStock")
                            .child("as" + count).child("aheadStockName")
                            .setValue(aheadStockName.text.toString())
                        databaseUser.child(userId.toString()).child(datkey).child("aheadStock")
                            .child("as" + count).child("aheadStockNum").setValue(aheadStockNum.text.toString())
                        databaseUser.child(userId.toString()).child(datkey).child("aheadStock")
                            .child("as" + count).child("aheadStockPrice")
                            .setValue(aheadStockPrice.text.toString())
                    }
                    aheadAdapter.notifyDataSetChanged()
                    aheadAdapter.notifyDataSetInvalidated()

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
