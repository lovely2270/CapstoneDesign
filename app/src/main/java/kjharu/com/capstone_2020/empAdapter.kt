package kjharu.com.capstone_2020

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_join.view.*

class empAdapter(context: Context, var empList: ArrayList<Emp>, var userId : String?) : BaseAdapter(){
    private val mContext = context
    //private val mItem =

    val firebaseReference: FirebaseDatabase = FirebaseDatabase.getInstance()
    val databaseUser = firebaseReference.reference.child("user")

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        /* LayoutInflater는 item을 Adapter에서 사용할 View로 부풀려주는(inflate) 역할을 한다. */
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.listview_emp, null)

        /* 위에서 생성된 view를 res-layout-main_lv_item.xml 파일의 각 View와 연결하는 과정이다. */
        val empName = view.findViewById<TextView>(R.id.empName)
        val empSalary = view.findViewById<TextView>(R.id.empSalary)
        val empTime = view.findViewById<TextView>(R.id.empTime)
        val empType =  view.findViewById<TextView>(R.id.empType)


        /* ArrayList<Dog>의 변수 dog의 이미지와 데이터를 ImageView와 TextView에 담는다. */
        val emp = empList[position]
        empName.text = emp.name
        empSalary.text = emp.salary
        empTime.text = emp.time
        empType.text = emp.type

        return view
    }

    override fun getItem(position: Int): Any {
        return empList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return empList.size
    }
}