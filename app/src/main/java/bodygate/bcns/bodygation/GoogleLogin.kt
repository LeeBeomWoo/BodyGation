package bodygate.bcns.bodygation

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.support.v4.content.res.TypedArrayUtils.getString
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.google_login.*

/**
 * Created by LeeBeomWoo on 2018-03-16.
 */
class CustomDialogList(context: Context): Dialog(context) {
    val TAG: String = "CustomDialogList_"
    private val RC_SIGN_IN = 111//google sign in request code
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.google_login)
    }
}