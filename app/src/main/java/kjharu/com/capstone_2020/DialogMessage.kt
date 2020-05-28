package kjharu.com.capstone_2020

import android.app.Activity
import android.app.AlertDialog


fun Dialogmessage(context: Activity, title: String, message: String,close:String?=null) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle(title)
    builder.setMessage(message)

    builder.setNegativeButton("취소",null)
    if(close==null) {

        builder.setPositiveButton("확인", null)
    }
    else if(close.equals("close")) {
        builder.setPositiveButton("확인") { dialogInterface, i ->
            context.finish()
        }
    }
    builder.show()
}
