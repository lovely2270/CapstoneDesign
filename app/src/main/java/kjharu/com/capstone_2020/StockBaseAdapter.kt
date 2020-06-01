package kjharu.com.capstone_2020

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.google.firebase.database.*

class StockBaseAdapter(context: Context, var StockList: ArrayList<Stock>, var userId : String?) : BaseAdapter(){
    private val mContext = context
    //private val mItem =

    val firebaseReference: FirebaseDatabase = FirebaseDatabase.getInstance()
    val databaseUser = firebaseReference.reference.child("user")

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        /* LayoutInflater는 item을 Adapter에서 사용할 View로 부풀려주는(inflate) 역할을 한다. */
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.listview_item, null)

        /* 위에서 생성된 view를 res-layout-main_lv_item.xml 파일의 각 View와 연결하는 과정이다. */
        val stockName= view.findViewById<TextView>(R.id.stockName)
        val stockCount = view.findViewById<TextView>(R.id.stockCount)
        val stockPrice = view.findViewById<TextView>(R.id.stockPrice)

        /* ArrayList<Dog>의 변수 dog의 이미지와 데이터를 ImageView와 TextView에 담는다. */
        val stock = StockList[position]
        stockName.text = stock.name
        stockCount.text = stock.num
        stockPrice.text = stock.price

        return view
    }

    override fun getItem(position: Int): Any {
        return StockList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return StockList.size
    }

}