package kjharu.com.capstone_2020

import android.app.AlertDialog
import android.content.Intent
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
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_stock.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class StockFragment(var userId : String?) : Fragment() {

    val firebaseReference: FirebaseDatabase = FirebaseDatabase.getInstance()
    val databaseUser = firebaseReference.reference.child("user")
    var stockList = ArrayList<Stock>()
    var sCount : Int = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState)
        var inflaterview = inflater.inflate(R.layout.fragment_stock, container, false)

        //년도월 키 받아오기
        val current = LocalDate.now()
        val datkey =  current.format(DateTimeFormatter.ofPattern("yyyyMM"))
        val databaseYM = databaseUser.child(userId.toString()).child(datkey)

        //val item = Array(20, {""})
        val adapter = StockBaseAdapter(context!!,stockList,userId)
        val lv = inflaterview.findViewById(R.id.listView) as ListView
        lv.setAdapter(adapter)

        //리스트 불러주기
        databaseUser.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                stockList.clear()
                //재고 경로
                var databaseStock = p0.child(userId.toString()).child(datkey).child("stock")

                for (i in 0..databaseStock.childrenCount - 2) {
                    var stock : Stock = Stock("","","")
                    stock.name = databaseStock.child("s"+i).child("stockName").value.toString()
                    stock.num = databaseStock.child("s"+i).child("stockNum").value.toString()
                    stock.price = databaseStock.child("s"+i).child("stockPrice").value.toString()

                    stockList.add(stock)

                    adapter.notifyDataSetChanged()
                    adapter.notifyDataSetInvalidated()

                }
                //sCount = Integer.parseInt(databaseStock.child("sCount").value.toString())

            }
            override fun onCancelled(p0: DatabaseError) {

            }


        })
        
        //클릭리스너 (수정 시)
        lv.onItemClickListener= AdapterView.OnItemClickListener { parent, view, position, id ->
            databaseUser.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    var databaseStock = p0.child(userId.toString()).child(datkey).child("stock")


                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                }
            })
        }

        //길게 누르면 (삭제)
        lv.setOnItemLongClickListener({ parent, view, position, id ->
            val builder = AlertDialog.Builder(context)
            builder.setTitle("경고")
            builder.setMessage("*Id를 삭제하시겠습니까? 모든 내용이 삭제됩니다.*")

            builder.setNegativeButton("취소", null)
            builder.setPositiveButton("확인") { dialogInterface, i ->
                stockList.removeAt(position)
                databaseUser.child(userId.toString()).child(datkey).child("stock").child("s"+position).removeValue()

                adapter.notifyDataSetChanged()
                adapter.notifyDataSetInvalidated()
            }

            builder.show()
            true
        })

        //재고 추가 버튼 클릭
        var btn_addStock : Button = inflaterview.findViewById(R.id.btn_addStock)
        btn_addStock.setOnClickListener {
            val dialogView = inflater.inflate(R.layout.stock_add, null)

            val builder = AlertDialog.Builder(context)
            builder.setTitle("재고 추가")



            val stockName = dialogView.findViewById<EditText>(R.id.stockNameForAdd)
            val stockNum = dialogView.findViewById<EditText>(R.id.stockNumForAdd)
            val stockPrice = dialogView.findViewById<EditText>(R.id.stockPriceForAdd)

            builder.setNegativeButton("취소", null)
            builder.setPositiveButton("확인") { dialogInterface, i ->
                var stock : Stock = Stock(stockName.text.toString(),stockNum.text.toString(),stockPrice.text.toString())
                //리스트에 추가
                stockList.add(stock)
                //DB에 저장
              /*  databaseUser.child(userId.toString()).child(datkey).child("stock").child("s"+sCount).child("stockName").setValue(stockName.text.toString())
                databaseUser.child(userId.toString()).child(datkey).child("stock").child("s"+sCount).child("stockNum").setValue(stockNum.text.toString())
                databaseUser.child(userId.toString()).child(datkey).child("stock").child("s"+sCount).child("stockPrice").setValue(stockPrice.text.toString())

                //카운트 추가
                databaseUser.child(userId.toString()).child(datkey).child("stock").child("sCount").setValue(sCount+1)
                */
                adapter.notifyDataSetChanged()
                adapter.notifyDataSetInvalidated()
            }

            builder.show()
        }

        return inflaterview
    }
}