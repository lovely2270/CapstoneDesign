package kjharu.com.capstone_2020

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.google.firebase.database.*

class StockBaseAdapter(context: Context, item : Array<String>) : BaseAdapter(){
    private val mContext = context
    private val mItem = item

    val firebaseReference: FirebaseDatabase = FirebaseDatabase.getInstance()
    val databaseStock = firebaseReference.reference.child("stock")

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        lateinit var viewHolder : ViewHolder
        var view = convertView
        if (view == null){
            viewHolder = ViewHolder()
            view = LayoutInflater.from(mContext).inflate(R.layout.listview_item,parent,false)
           // viewHolder.button = view.findViewById(R.id.button)
            viewHolder.textView = view.findViewById(R.id.stockName)
            viewHolder.textView = view.findViewById(R.id.stockCount)
            viewHolder.textView = view.findViewById(R.id.stockPrice)
            view.tag = viewHolder
            viewHolder.textView.text = mItem[position]
            return view
        }else{
            viewHolder = view.tag as ViewHolder
        }
        viewHolder.textView.text = mItem[position]
        return  view
    }

    override fun getItem(position: Int) = mItem[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = mItem.size

    inner class ViewHolder{
        lateinit var textView : TextView
        lateinit var button : Button
    }
}