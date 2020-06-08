package kjharu.com.capstone_2020

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_menu.*


class MenuActivity : AppCompatActivity() {
    val firebaseReference: FirebaseDatabase = FirebaseDatabase.getInstance()
    val databaseUser = firebaseReference.reference.child("user")

    var userId: String? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        setTitle("떡잎방범대")
        //LoginActivity에서 넘겨준 변수 받아오기
        userId = intent.getStringExtra("userId")

        //탭뷰만들어줌
        val fragmentAdapter = MyPagerAdapter(supportFragmentManager, userId)
        viewpager.adapter = fragmentAdapter
        tab.setupWithViewPager(viewpager)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.mypage,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId) {
            R.id.mypaceIcon -> {
                val intent = Intent(this@MenuActivity, MyPageActivity::class.java)
                //id넘김=>얘로 나중에 사용자누군지 계속 구분해야지
                intent.putExtra("userId",userId)
                startActivity(intent)
                return true}
        }
        return super.onOptionsItemSelected(item)
    }

}