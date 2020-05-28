package kjharu.com.capstone_2020

import android.R.attr.fragment
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


class MyPagerAdapter(fm : FragmentManager, userId : String?) : FragmentPagerAdapter(fm) {

    var userId = userId
    override fun getItem(position: Int): Fragment {
        val bundle = Bundle(1) // 파라미터는 전달할 데이터 개수
        bundle.putString("userId", userId) // key , value

        if (position == 0) {
            return StockFragment(userId)
        } else if (position == 1) {
            return AheadFragment(userId)
        } else if (position == 2) {
            return EmpFragment(userId)
        } else if (position == 3) {
            return SalesFragment(userId)
        }
        return StockFragment(userId)
    }

    override fun getCount(): Int {
        return 4 //4개니깐
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position){
            0 -> "재고관리"
            1 -> "입고예정"
            2 -> "직원관리"
            else -> {return "매출정보"}
        }
    }

}