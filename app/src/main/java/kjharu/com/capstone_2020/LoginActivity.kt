package kjharu.com.capstone_2020

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.widget.Toast

/**
 * Created by lovel on 2019-10-21.
 */
class LoginActivity : AppCompatActivity() {
    val firebaseReference: FirebaseDatabase = FirebaseDatabase.getInstance()
    val databaseUser = firebaseReference.reference.child("user")
    //입력한 Id를 가진 유저가 있는지 플래그
    var findId= false

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setTitle("떡잎방범대")

        creatuserbtn.setOnClickListener {
            val intent = Intent(this, JoinActivity::class.java)
            startActivity(intent)
        }

        //현재 로그인시
        loginbtn.setOnClickListener {
            databaseUser.addListenerForSingleValueEvent(checkLogin)
        }
    }
    //로그인 확인
    //id가 존재하는지 확인
    val checkLogin = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (child in dataSnapshot.children) {
                //아이디있나
                if (child.key.toString().equals(editTextID.text.toString())) {
                    findId = true
                    //비번맞으면
                    if(child.child("pw").value.toString().equals(editTextPW.text.toString())){

                        //맞으면 메뉴 페이지로
                        val intent2 = Intent(this@LoginActivity, MenuActivity::class.java)
                        //id넘김=>얘로 나중에 사용자누군지 계속 구분해야지
                        intent2.putExtra("userId",child.key.toString())
                        //Toast.makeText(applicationContext, child.key.toString(), Toast.LENGTH_LONG).show()
                        startActivity(intent2)

                    }
                    //비번틀리면
                    else{
                        Dialogmessage(this@LoginActivity,"경고","비밀번호를 확인해주세요.")
                    }
                }

            }
            //이멜 못찾음
            if(!findId){
                Dialogmessage(this@LoginActivity,"경고","존재하지 않는 아이디입니다.")
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    }
}