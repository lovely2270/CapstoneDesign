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
import kotlinx.android.synthetic.main.fragment_sales.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SalesFragment(var userId : String?) : Fragment() {

    val firebaseReference: FirebaseDatabase = FirebaseDatabase.getInstance()
    val databaseUser = firebaseReference.reference.child("user")
    //val userId =
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val context = container?.context

        //년도월 키 받아오기
        val current = LocalDate.now()
        val datkey =  current.format(DateTimeFormatter.ofPattern("yyyyMM"))
        val databaseYM = databaseUser.child(userId.toString()).child(datkey)

        super.onCreateView(inflater, container, savedInstanceState)
        var inflaterview = inflater.inflate(R.layout.fragment_sales, container, false)

        //(이름)님의
        var textViewName : TextView = inflaterview.findViewById(R.id.textViewName)
        textViewName.setText(userId+" 님의")

        //()년 ()월 순이익

        val formatter = current.format(DateTimeFormatter.ofPattern("yyyy년 MM월"))
        var textViewDate : TextView = inflaterview.findViewById(R.id.textViewDate)
        textViewDate.setText(formatter + " 순이익")

        //()원 = 매출 - 매장월세 - 직원급여 - 재고비용
        var textViewM : TextView = inflaterview.findViewById(R.id.textViewM)

        //매출액
        //초기값은 0
        var textViewSales : TextView = inflaterview.findViewById(R.id.textViewSales)
        textViewSales.setText("0")

        //매장월세
        var textViewRent : TextView = inflaterview.findViewById(R.id.textViewRent)

        //직원급여
        var textViewSalary : TextView = inflaterview.findViewById(R.id.textViewSalary)

        //재고비용
        var textViewStock : TextView = inflaterview.findViewById(R.id.textViewStock)

        //매출입력 버튼 클릭
        var btn_putSales : Button = inflaterview.findViewById(R.id.btn_putSales)
        btn_putSales.setOnClickListener {
            //팝업창띄워서 판매액 받기
            val builder = AlertDialog.Builder(context)
            builder.setTitle("판매액 입력")
            builder.setMessage("판매액")

            var editTextSales : EditText = EditText(context)
            editTextSales.setHint("숫자입력")
            builder.setView(editTextSales)

            builder.setNegativeButton("취소", null)

            builder.setPositiveButton("확인") { dialogInterface, i ->
                //입력되면 매출액 변경
                if (editTextSales.text != null){
                    databaseYM.child("sales").setValue(editTextSales.text.toString())
                }
                else{

                }
            }
            builder.show()

        }

        //데이터값 변경될 시 리스너
        var btn_refresh : ImageButton = inflaterview.findViewById(R.id.imgbtn_refresh)
        //var btn_refresh : Button = inflaterview.findViewById(R.id.btn_refresh)
        btn_refresh.setOnClickListener{
            databaseUser.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    //매출액 필드 값 변경
                    //user -> userId -> datekey -> sales 의 값
                    var sales = p0.child(userId.toString()).child(datkey).child("sales").value
                    if (sales == null) {
                        sales = 0
                    }
                    textViewSales.setText(sales.toString())

                    //매장월세 변경
                    //user -> userId -> rentPrice 의 값
                    var rent = p0.child(userId.toString()).child("rentPrice").value
                    if (rent == null) {
                        rent = 0
                    }
                    textViewRent.setText(rent.toString())

                    //직원급여 변경
                    //user -> userId -> datekey -> emp 의 자식 수세서 for문
                    var databaseEmp = p0.child(userId.toString()).child(datkey).child("emp")
                    //직원 수 만큼 돌려
                    //총금액변수
                    var salaryM: Int = 0
                    for (i in 0..databaseEmp.childrenCount - 1) {
                        //현재 직원의 정보
                        var databaseNowEmp = databaseEmp.child("e" + i)
                        //직원 정보의 시급 * 시간
                        var money =
                            Integer.parseInt(databaseNowEmp.child("empSalary").value.toString()) * Integer.parseInt(
                                databaseNowEmp.child("empTime").value.toString()
                            )
                        //총 직원 급여 변수에 현재 직원 급여 더하기
                        salaryM += money
                    }
                    //textview에 직원 급여 입력
                    textViewSalary.setText(salaryM.toString())

                    /*
                    //재고비용 변경 (재고 + 예정재고)
                    //재고 총 비용
                    var databaseStock = p0.child(userId.toString()).child(datkey).child("stock")
                    var stockCost: Int = 0
                    for (i in 0..databaseStock.childrenCount - 1) {
                        //현재 재고의 정보
                        var databaseNowStock = databaseStock.child("s" + i)
                        //재고 정보의 가격 * 개수
                        //재고 개수
                        var stockNum =
                            Integer.parseInt(databaseNowStock.child("stockNum").value.toString())
                        //재고 가격
                        var stockPrice =
                            Integer.parseInt(databaseNowStock.child("stockPrice").value.toString())

                        var money = stockNum * stockPrice
                        //총 직원 급여 변수에 현재 직원 급여 더하기
                        stockCost += money
                    }
                    */


                    //입고예정 재고 총 비용
                    var databaseAheadStock =
                        p0.child(userId.toString()).child(datkey).child("aheadStock")
                    var aheadStockCost: Int = 0
                    for (i in 0..databaseAheadStock.childrenCount - 1) {
                        //현재 재고의 정보
                        var databaseNowAheadStock = databaseAheadStock.child("as" + i)
                        //재고 정보의 가격 * 개수
                        var money =
                            Integer.parseInt(databaseNowAheadStock.child("aheadStockNum").value.toString()) * Integer.parseInt(
                                databaseNowAheadStock.child("aheadStockPrice").value.toString()
                            )                    //총 직원 급여 변수에 현재 직원 급여 더하기
                        aheadStockCost += money
                    }

                    //두개 합친거
                    //var allStock: Int = stockCost + aheadStockCost
                    //textview에 재고비용 입력
                    textViewStock.setText(aheadStockCost.toString())


                    //원 계산(매출액 - (매장월세 + 직원급여 + 재고비용))
                    var profit: Int =
                        Integer.parseInt(sales.toString()) - (Integer.parseInt(rent.toString()) + salaryM + aheadStockCost)
                    textViewM.setText(profit.toString() + " 원")

                    //순수익 파이어베이스에 저장
                    //순수익 가져오기
                    var amountM = textViewM.text
                    databaseYM.child("amount").setValue(amountM)
                }

                override fun onCancelled(p0: DatabaseError) {

                }


            })
        }

        databaseUser.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                //매출액 필드 값 변경
                //user -> userId -> datekey -> sales 의 값
                var sales = p0.child(userId.toString()).child(datkey).child("sales").value
                if (sales == null) {
                    sales = 0
                }
                textViewSales.setText(sales.toString())

                //매장월세 변경
                //user -> userId -> rentPrice 의 값
                var rent = p0.child(userId.toString()).child("rentPrice").value
                if (rent == null) {
                    rent = 0
                }
                textViewRent.setText(rent.toString())

                //직원급여 변경
                //user -> userId -> datekey -> emp 의 자식 수세서 for문
                var databaseEmp = p0.child(userId.toString()).child(datkey).child("emp")
                //직원 수 만큼 돌려
                //총금액변수
                var salaryM: Int = 0
                for (i in 0..databaseEmp.childrenCount - 1) {
                    //현재 직원의 정보
                    var databaseNowEmp = databaseEmp.child("e" + i)
                    //직원 정보의 시급 * 시간
                    var money =
                        Integer.parseInt(databaseNowEmp.child("empSalary").value.toString()) * Integer.parseInt(
                            databaseNowEmp.child("empTime").value.toString()
                        )
                    //총 직원 급여 변수에 현재 직원 급여 더하기
                    salaryM += money
                }
                //textview에 직원 급여 입력
                textViewSalary.setText(salaryM.toString())

                /*
                //재고비용 변경 (재고 + 예정재고)
                //재고 총 비용
                var databaseStock = p0.child(userId.toString()).child(datkey).child("stock")
                var stockCost: Int = 0
                for (i in 0..databaseStock.childrenCount - 1) {
                    //현재 재고의 정보
                    var databaseNowStock = databaseStock.child("s" + i)
                    //재고 정보의 가격 * 개수
                    //재고 개수
                    var stockNum =
                        Integer.parseInt(databaseNowStock.child("stockNum").value.toString())
                    //재고 가격
                    var stockPrice =
                        Integer.parseInt(databaseNowStock.child("stockPrice").value.toString())

                    var money = stockNum * stockPrice
                    //총 직원 급여 변수에 현재 직원 급여 더하기
                    stockCost += money
                }
                */

                //입고예정 재고 총 비용
                var databaseAheadStock =
                    p0.child(userId.toString()).child(datkey).child("aheadStock")
                var aheadStockCost: Int = 0
                for (i in 0..databaseAheadStock.childrenCount - 1) {
                    //현재 재고의 정보
                    var databaseNowAheadStock = databaseAheadStock.child("as" + i)
                    //재고 정보의 가격 * 개수
                    var money =
                        Integer.parseInt(databaseNowAheadStock.child("aheadStockNum").value.toString()) * Integer.parseInt(
                            databaseNowAheadStock.child("aheadStockPrice").value.toString()
                        )                    //총 직원 급여 변수에 현재 직원 급여 더하기
                    aheadStockCost += money
                }

                //두개 합친거
                //var allStock: Int = stockCost + aheadStockCost
                //textview에 재고비용 입력
                textViewStock.setText(aheadStockCost.toString())


                //원 계산(매출액 - (매장월세 + 직원급여 + 재고비용))
                var profit: Int =
                    Integer.parseInt(sales.toString()) - (Integer.parseInt(rent.toString()) + salaryM + aheadStockCost)
                textViewM.setText(profit.toString() + " 원")

                //순수익 파이어베이스에 저장
                //순수익 가져오기
                var amountM = textViewM.text
                databaseYM.child("amount").setValue(amountM)
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })

        return inflaterview
    }

}