package kjharu.com.capstone_2020

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_change.*

class ChangeActivity : AppCompatActivity() {
    val firebaseReference: FirebaseDatabase = FirebaseDatabase.getInstance()
    val databaseUser = firebaseReference.reference.child("user")

    var userId: String? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change)

        userId = intent.getStringExtra("userId")

        //취소
        cancelbtnForC.setOnClickListener {
            finish()
        }

        //회원수정
        changebtn.setOnClickListener {
            //비번같은지 확인
            if (edittextNewPW1ForC.text.toString() == edittextNewPW2ForC.text.toString()) {
                //id 안에 이름넣어줘
                databaseUser.child(userId.toString()).child("name").setValue(editTextNameForC.text.toString())
                //id안에 비번넣어줘
                databaseUser.child(userId.toString()).child("pw").setValue(edittextNewPW1ForC.text.toString())
                //id안에 매장월세 넣어줘
                databaseUser.child(userId.toString()).child("rentPrice").setValue(editTextRentPriceForC.text.toString())

                Dialogmessage(this@ChangeActivity,"알림","수정이 완료되었습니다.", "close")
            } else {
                //비번두개가 동일하지않아.
                Dialogmessage(this@ChangeActivity,"경고","비번 확인해주세요.")
            }
        }

    }
}
