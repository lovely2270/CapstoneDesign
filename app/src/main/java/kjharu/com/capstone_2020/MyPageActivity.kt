package kjharu.com.capstone_2020

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_mypage.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MyPageActivity : AppCompatActivity() {
    val firebaseReference: FirebaseDatabase = FirebaseDatabase.getInstance()
    val databaseUser = firebaseReference.reference.child("user")

    var userId: String? = null

    @RequiresApi(Build.VERSION_CODES.O)
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)
        setTitle("떡잎방범대 마이페이지")
        userId = intent.getStringExtra("userId")

        //년도월 키 받아오기
        val current = LocalDate.now()
        val datkey =  current.format(DateTimeFormatter.ofPattern("yyyyMM"))
        val databaseYM = databaseUser.child(userId.toString()).child(datkey)

        //btn_change 리스너 달기 (회원수정)
        btn_change.setOnClickListener {
            val intent = Intent(this, ChangeActivity::class.java)
            intent.putExtra("userId",userId)
            startActivity(intent)
        }
        
        //btn_reset 리스너 달기 (직원, 재고 내용 삭제)
        btn_reset.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("경고")
            builder.setMessage("*직원, 재고정보를 정말로 삭제하시겠습니까?*")

            builder.setNegativeButton("취소",null)
            builder.setPositiveButton("확인") { dialogInterface, i ->

                //직원 내용 전체 삭제
                databaseYM.child("emp").removeValue()
                //재고 내용 전체 삭제
                databaseYM.child("stock").removeValue()
                //입고예정 내용 전체 삭제
                databaseYM.child("aheadStock").removeValue()

                val intent = Intent(this, LoginActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent)
                this.finish()
            }
            builder.show()


        }
        
        //btn_delete 리스너 달기 (해당 id유저 걍 다 삭제)
        btn_delete.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            builder.setTitle("경고")
            builder.setMessage("*Id를 삭제하시겠습니까? 모든 내용이 삭제됩니다.*")

            builder.setNegativeButton("취소",null)
            builder.setPositiveButton("확인") { dialogInterface, i ->
                databaseUser.child(userId.toString()).removeValue()
                val intent = Intent(this, LoginActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent)
                this.finish()
            }
            builder.show()

        }

        //데이터값 변경될 시 리스너
        databaseUser.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                //매장월세 변경
                //user -> userId -> rentPrice 의 값
                var name = p0.child(userId.toString()).child("name").value
                textViewNameforMP.setText(name.toString()+ "님!")

            }
            override fun onCancelled(p0: DatabaseError) {

            }


        })
    }
}