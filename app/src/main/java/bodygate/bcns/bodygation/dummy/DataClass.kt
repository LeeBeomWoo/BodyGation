package bodygate.bcns.bodygation.dummy

import com.github.mikephil.charting.data.BarEntry

class DataClass(gs:MutableList<BarEntry>, cs:MutableList<BarEntry>, ks:MutableList<BarEntry>, ts:MutableList<BarEntry>, ms:MutableList<BarEntry>, ls:MutableList<BarEntry>
                , gl:MutableList<String>, cl:MutableList<String>, kl:MutableList<String>, tl:MutableList<String>, ml:MutableList<String>, ll:MutableList<String>) {

    var weight_series: MutableList<BarEntry> = ArrayList()
    var muscle_series: MutableList<BarEntry> = ArrayList()
    var walk_series: MutableList<BarEntry> = ArrayList()
    var fat_series: MutableList<BarEntry> = ArrayList()
    var bmi_series: MutableList<BarEntry> = ArrayList()
    var kcal_series: MutableList<BarEntry> = ArrayList()

    var weight_Label:MutableList<String> =  ArrayList()
    var kcal_Label:MutableList<String> =  ArrayList()
    var walk_Label:MutableList<String> =  ArrayList()
    var fat_Label:MutableList<String> =  ArrayList()
    var muscle_Label:MutableList<String> =  ArrayList()
    var bmi_Label:MutableList<String> =  ArrayList()
    init{
        weight_series = gs
        muscle_series = cs
        walk_series = ks
        fat_series = ts
        bmi_series = ms
        kcal_series = ls

        weight_Label = gl
        kcal_Label =  cl
        walk_Label =  kl
        fat_Label =  tl
        muscle_Label = ml
        bmi_Label = ll
    }
}