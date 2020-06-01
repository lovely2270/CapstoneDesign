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
import com.google.firebase.database.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class StockFragment(var userId : String?) : Fragment() {

    val firebaseReference: FirebaseDatabase = FirebaseDatabase.getInstance()
    val databaseUser = firebaseReference.reference.child("user")
    var stockList = ArrayList<Stock>()
    //현재 stock의 갯수 - > 추가할때 이거씀
    var count = 0

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

        val adapter = StockBaseAdapter(context!!,stockList,userId)
        val lv = inflaterview.findViewById(R.id.listView) as ListView
        lv.setAdapter(adapter)

        //리스트 불러주기
        databaseUser.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                //stockList비우기
                stockList.clear()
                //재고 경로
                var databaseStock = p0.child(userId.toString()).child(datkey).child("stock")

                for (i in 0..databaseStock.childrenCount - 1) {
                    var stock : Stock = Stock("","","")
                    stock.name = databaseStock.child("s"+i).child("stockName").value.toString()
                    stock.num = databaseStock.child("s"+i).child("stockNum").value.toString()
                    stock.price = databaseStock.child("s"+i).child("stockPrice").value.toString()

                    stockList.add(stock)

                    adapter.notifyDataSetChanged()
                    adapter.notifyDataSetInvalidated()

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
                    val dialogView = inflater.inflate(R.layout.stock_add, null)

                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("재고 수정")
                    builder.setView(dialogView)

                    //현재 수정하려는 재고의 값들
                    //현재 재고명
                    var nameNow = p0.child(userId.toString()).child(datkey).child("stock").child("s"+position).child("stockName").value
                    //현재 재고량
                    var numNow = p0.child(userId.toString()).child(datkey).child("stock").child("s"+position).child("stockNum").value
                    //현재 재고원가
                    var priceNow = p0.child(userId.toString()).child(datkey).child("stock").child("s"+position).child("stockPrice").value

                    val stockName = dialogView.findViewById<EditText>(R.id.stockNameForAdd)
                    stockName.setText(nameNow.toString())
                    val stockNum = dialogView.findViewById<EditText>(R.id.stockNumForAdd)
                    stockNum.setText(numNow.toString())
                    val stockPrice = dialogView.findViewById<EditText>(R.id.stockPriceForAdd)
                    stockPrice.setText(priceNow.toString())

                    builder.setNegativeButton("취소", null)
                    builder.setPositiveButton("확인") { dialogInterface, i ->

                        //DB에 저장
                        databaseUser.child(userId.toString()).child(datkey).child("stock").child("s"+position).child("stockName").setValue(stockName.text.toString())
                        databaseUser.child(userId.toString()).child(datkey).child("stock").child("s"+position).child("stockNum").setValue(stockNum.text.toString())
                        databaseUser.child(userId.toString()).child(datkey).child("stock").child("s"+position).child("stockPrice").setValue(stockPrice.text.toString())

                    }

                    adapter.notifyDataSetChanged()
                    adapter.notifyDataSetInvalidated()

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
                    val databaseStock = databaseUser.child(userId.toString()).child(datkey).child("stock")

                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("경고")
                    builder.setMessage("*현 재고를 삭제하시겠습니까? 모든 내용이 삭제됩니다.*")

                    builder.setNegativeButton("취소", null)
                    builder.setPositiveButton("확인") { dialogInterface, i ->

                        val databaseStockForGet = p0.child(userId.toString()).child(datkey).child("stock")
                        //하나씩 앞당겨주기
                        for (i in position..databaseStockForGet.childrenCount - 1) {
                            //다음 이름 가져오기
                            var nextName = databaseStockForGet.child("s" + (i + 1)).child("stockName").value.toString()
                            //앞당겨저장
                            databaseStock.child("s" + i).child("stockName").setValue(nextName)
                            //다음 재고량 가져오기
                            var nextNum = databaseStockForGet.child("s" + (i + 1)).child("stockNum").value.toString()
                            //앞당겨저장
                            databaseStock.child("s" + i).child("stockNum").setValue(nextNum)
                            //다음 재고원가 가져오기
                            var nextPrice = databaseStockForGet.child("s" + (i + 1))
                                .child("stockPrice").value.toString()
                            //앞당겨저장
                            databaseStock.child("s" + i).child("stockPrice").setValue(nextPrice)

                        }
                        //맨 마지막꺼 없애기(앞당겨줬으니)
                        databaseUser.child(userId.toString()).child(datkey).child("stock").child("s" + (databaseStockForGet.childrenCount - 1)).removeValue()


                    }
                    adapter.notifyDataSetChanged()
                    adapter.notifyDataSetInvalidated()

                    builder.show()

                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                }

            })
            true
        })

        //재고 추가 버튼 클릭
        var btn_addStock : Button = inflaterview.findViewById(R.id.btn_addStock)
        btn_addStock.setOnClickListener {
            databaseUser.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val dialogView = inflater.inflate(R.layout.stock_add, null)

                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("재고 추가")
                    builder.setView(dialogView)

                    val stockName = dialogView.findViewById<EditText>(R.id.stockNameForAdd)
                    val stockNum = dialogView.findViewById<EditText>(R.id.stockNumForAdd)
                    val stockPrice = dialogView.findViewById<EditText>(R.id.stockPriceForAdd)

                    builder.setNegativeButton("취소", null)
                    builder.setPositiveButton("확인") { dialogInterface, i ->

                        //DB에 저장
                        databaseUser.child(userId.toString()).child(datkey).child("stock").child("s"+count).child("stockName").setValue(stockName.text.toString())
                        databaseUser.child(userId.toString()).child(datkey).child("stock").child("s"+count).child("stockNum").setValue(stockNum.text.toString())
                        databaseUser.child(userId.toString()).child(datkey).child("stock").child("s"+count).child("stockPrice").setValue(stockPrice.text.toString())


                    }
                    adapter.notifyDataSetChanged()
                    adapter.notifyDataSetInvalidated()

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