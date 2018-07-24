package bodygate.bcns.bodygation.support

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import bodygate.bcns.bodygation.dummy.DataClass
import com.github.mikephil.charting.data.BarEntry
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.*
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.request.DataTypeCreateRequest
import com.google.android.gms.fitness.result.DataReadResponse
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.fragment_goal.*
import kotlinx.android.synthetic.main.maintablayout.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

interface FitConnect{
    val TAG: String
        get() = "FitConnect"
    var account:GoogleSignInAccount
    var  mAuth: FirebaseAuth
    var personUrl:Uri
    var data:DataClass

    fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            accessGoogleFit(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
            getProfileInformation(null)
        }

    }
    private fun getProfileInformation(acct: FirebaseUser?) {
        //if account is not null fetch the information
        if (acct != null) {
            //user display name
            val personName = acct.getDisplayName()


            //user email id
            val personEmail = acct.getEmail()

            //user profile pic
            personUrl = acct.photoUrl!!
            Log.i("profile", "getProfileInformation : " + personName + "\t" + acct.photoUrl.toString() + "\t"+ personEmail +"\t" + acct.toString())
        }
    }

    fun accessGoogleFit(acc:GoogleSignInAccount){
        val credential = GoogleAuthProvider.getCredential(acc.idToken, null)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(object : OnCompleteListener<AuthResult>{
                    override fun onComplete(p0: Task<AuthResult>) {
                        if(p0.isSuccessful){
                            val user = mAuth.currentUser
                            getProfileInformation(user!!)
                        }else{
                            getProfileInformation(null)
                        }
                    }
                })
    }
}