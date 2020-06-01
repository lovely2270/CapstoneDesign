package kjharu.com.capstone_2020


import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_join.*
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener


class JoinActivity : AppCompatActivity() {
    val firebaseReference: FirebaseDatabase = FirebaseDatabase.getInstance()
    val databaseUser = firebaseReference.reference.child("user")
    var overlap: Boolean = false //Id중복인지

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        //아이디칸 누르면 바꾼가능성이 있느니 다시 중복체크하도록
        editTextNewID.setOnClickListener {
            overlap = false
        }

        //아이디 중복체크
        checkOverlapBtn.setOnClickListener {
            databaseUser.addListenerForSingleValueEvent(checkRegister)
        }

        //취소
        cancelbtn.setOnClickListener {
            finish()
        }

        //회원가입
        joinbtn.setOnClickListener {
            if (overlap) {
                //비번같은지 확인
                if (edittextNewPW1.text.toString() == edittextNewPW2.text.toString()) {
                    //id넣어줘
                    databaseUser.child(editTextNewID.text.toString()).setValue(null)
                    //id 안에 이름넣어줘
                    databaseUser.child(editTextNewID.text.toString()).child("name").setValue(editTextName.text.toString())
                    //id안에 비번넣어줘
                    databaseUser.child(editTextNewID.text.toString()).child("pw").setValue(edittextNewPW1.text.toString())
                    //id안에 매장월세 넣어줘
                    databaseUser.child(editTextNewID.text.toString()).child("rentPrice").setValue(editTextRentPrice.text.toString())

                    Dialogmessage(this@JoinActivity,"알림","회원가입 되었어요.", "close")
                } else {
                    //비번두개가 동일하지않아.
                    Dialogmessage(this@JoinActivity,"경고","비번 확인해주세요.")
                }
            } else {
                //아이디 중복확인바람
                Dialogmessage(this@JoinActivity,"경고","이메일 중복 확인해주세요.")
            }
        }

        //키보드 숨기기
        ll_join.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(it.windowToken, 0)
        }

    }

    val checkRegister = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            this@JoinActivity.overlap = true
            for (child in dataSnapshot.children) {
                //id = dataSnapshot.childrenCount.toInt()
                val id = editTextNewID.text.toString()
                if (child.key.toString().equals(id)) {
                    this@JoinActivity.overlap = false
                }
            }
            if (overlap) {
                Dialogmessage(this@JoinActivity,"알림","사용가능한 아이디입니다.")
            } else {
                Dialogmessage(this@JoinActivity,"경고","중복된 아이디입니다.")
            }

        }

        override fun onCancelled(databaseError: DatabaseError) {}
    }
}
