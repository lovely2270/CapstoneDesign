package kjharu.com.capstone_2020

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.google.firebase.database.*

class StockFragment(var userId : String?) : Fragment() {

    private lateinit var database: DatabaseReference
    private val title_array = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState)
        var inflaterview = inflater.inflate(R.layout.fragment_stock, container, false)

        val item = Array(20, {""})
        val adapter = StockBaseAdapter(context!!,item)
        val lv = inflaterview.findViewById(R.id.listView) as ListView
        lv.setAdapter(adapter)
     /*   val item = arrayOf("리스트뷰","ListView","Adapter","어뎁터","ArrayAdapter")
        //ArrayAdapter 객체를 만들고 리스트뷰에 연결
        val adapter = ArrayAdapter(context!!,android.R.layout.simple_list_item_1,item)
        //여기에 코딩하면 돼
        val lv = inflaterview.findViewById(R.id.listView) as ListView
        lv.setAdapter(adapter)*/

        return inflaterview
    }
}