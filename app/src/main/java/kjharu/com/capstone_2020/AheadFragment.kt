package kjharu.com.capstone_2020

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * A simple [Fragment] subclass.
 */
class AheadFragment(var userId : String?) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState)
        var inflaterview = inflater.inflate(R.layout.fragment_ahead, container, false)

        //여기에 코딩하면 돼

        return inflaterview
    }

}
