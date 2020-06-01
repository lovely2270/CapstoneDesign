package kjharu.com.capstone_2020

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.google.firebase.database.FirebaseDatabase

class aheadStockAdapter(context: Context, var aheadStockList: ArrayList<aheadStock>, var userId : String?) : BaseAdapter(){
    private val mContext = context
    //private val mItem =

    val firebaseReference: FirebaseDatabase = FirebaseDatabase.getInstance()
    val databaseUser = firebaseReference.reference.child("user")

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        /* LayoutInflater는 item을 Adapter에서 사용할 View로 부풀려주는(inflate) 역할을 한다. */
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.listview_aheaditem, null)

        /* 위에서 생성된 view를 res-layout-main_lv_item.xml 파일의 각 View와 연결하는 과정이다. */
        val aheadStockDate= view.findViewById<TextView>(R.id.aheadStockDate)
        val aheadStockName= view.findViewById<TextView>(R.id.aheadStockName)
        val aheadStockNum = view.findViewById<TextView>(R.id.aheadStockNum)
        val aheadStockPrice = view.findViewById<TextView>(R.id.aheadStockPrice)

        /* ArrayList<Dog>의 변수 dog의 이미지와 데이터를 ImageView와 TextView에 담는다. */
        val aheadStock = aheadStockList[position]
        aheadStockDate.text = aheadStock.date
        aheadStockName.text = aheadStock.name
        aheadStockNum.text = aheadStock.num
        aheadStockPrice.text = aheadStock.price

        return view
    }

    override fun getItem(position: Int): Any {
        return aheadStockList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return aheadStockList.size
    }
}