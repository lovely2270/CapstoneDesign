package kjharu.com.capstone_2020

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class EmpFragment(var userId : String?) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState)
        var inflaterview = inflater.inflate(R.layout.fragment_emp, container, false)

        //여기에 코딩하면 돼

        return inflaterview
    }
}