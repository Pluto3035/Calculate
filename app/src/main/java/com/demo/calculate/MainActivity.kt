package com.demo.calculate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.text.StringBuilder

class MainActivity : AppCompatActivity() ,View.OnClickListener{
    var currentInputNumSB = StringBuilder()
    private val numsList = mutableListOf<Int>()
    private val operatorsList = mutableListOf<String>()
    private var isNumStart = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //清空按钮被点击
        AC.setOnClickListener {
            clearButtClick(it)
        }
        //撤销按钮
        back.setOnClickListener {
            backButtClick(it)
        }
        //运算符按钮
        jia.setOnClickListener {
            operatorButtClick(it)
        }
        jian.setOnClickListener {
            operatorButtClick(it)
        }
        cheng.setOnClickListener {
            operatorButtClick(it)
        }
        chu.setOnClickListener {
            operatorButtClick(it)
        }
        //数字
        zero.setOnClickListener(this)
        one.setOnClickListener(this)
        two.setOnClickListener(this)
        three.setOnClickListener(this)
        four.setOnClickListener(this)
        five.setOnClickListener(this)
        six.setOnClickListener(this)
        seven.setOnClickListener(this)
        eight.setOnClickListener(this)
        nine.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        numberButtClick(v!!)
    }

    //数字键
    fun numberButtClick(view:View){
        //将view强制转化为textView
        val tv = view as TextView
        currentInputNumSB.append(tv.text)
        if (isNumStart){
            //当前输入的数是一个新的数字，添加到数组中
            numsList.add(tv.text.toString().toInt())
            //更改状态，已经不是一个新数字的开始
            isNumStart = false
        }else{
            //用当前的数字去替换列表中最后一个元素
            numsList[numsList.size-1] = currentInputNumSB.toString().toInt()
        }
        //显示内容
        showUI()
        //计算结果
        calculate()

    }
    //运算符
    fun operatorButtClick(view: View){
        val tv = view as TextView
        //保存当前运算符
        operatorsList.add(tv.text.toString())
        //改变状态
        isNumStart = true
        currentInputNumSB.clear()

        showUI()
    }
    //请空键
    fun clearButtClick(view: View){
        process_textView.text = " "
        result_textView.text = "0 "
        currentInputNumSB.clear()
        numsList.clear()
        operatorsList.clear()
        isNumStart = true
    }
    //撤销键
    fun backButtClick(view: View){
        //判断应该撤销运算符还是数字
        if(numsList.size>operatorsList.size){
            //撤销数字
            if (numsList.size>0){
                numsList.removeLast()
                isNumStart = true
                currentInputNumSB.clear()
            }
        }else{
            //撤销运算符
            if (operatorsList.size>0){
                operatorsList.removeLast()
                isNumStart = false
                if (numsList.size>0){
                    currentInputNumSB.append(numsList.last())
                }
            }
        }
        showUI()
        calculate()
    }
    //=键
    fun equalButtClick(view: View){

    }

    //拼接当前运算的表达式，显示在界面上
   private fun showUI(){
        val str = StringBuilder()
        for((i,num)in numsList.withIndex()){
            //将当前数字拼接上去
            str.append(num)
            //判断运算符数组中对应位置是否有内容
            if (operatorsList.size>i){
                //将i对应的运算符拼接到字符串中
                str.append(" ${operatorsList[i]} ")
            }
        }
        process_textView.text = str.toString()
    }

    //实现逻辑运算功能
    private fun calculate(){
        if(numsList.size>0) {
            //记录运算符数组遍历时的下标
            var i = 0;
            //记录第一个运算数 = 数字数组第一个数
            var param1 = numsList[0].toFloat()
            var param2 = 0f
            if(operatorsList.size>0){
                while (true){
                    //获取i对应的运算符
                    val operator = operatorsList[i];
                    //是不是乘除
                    if(operator=="×"|| operator=="÷"){
                        //乘除直接运算
                        //找到第二个运算数
                            if(i+1<numsList.size){
                                param2 = numsList[i+1].toFloat();
                                param1 = realCalculate(param1,operator,param2)
                            }
                    }else{
                        //判断是不是最后一个，或者后面不是乘除
                        if(i==operatorsList.size-1||
                            (operatorsList[i+1]!="×"&&operatorsList[i+1]!="÷")){
                            //直接运算
                            if(i<numsList.size-1){
                                param2 = numsList[i+1].toFloat()
                                param1 = realCalculate(param1,operator,param2)
                            }
                        }else{
                            //后面有，而且是乘除
                            var j = i+1
                            var mparam1 = numsList[j].toFloat()
                            var mparam2 = 0.0f
                            while(true){
                                //获取j对应的运算符
                                if (operatorsList[j]=="×"||operatorsList[j]=="÷"){
                                    if(j<operatorsList.size-1){
                                        mparam2 = numsList[j+1].toFloat()
                                        mparam1 = realCalculate(mparam1,operatorsList[j],mparam2)
                                    }
                                }else{
                                    //之前那个运算符后面所有连续的乘除都运算结束
                                    break
                                }
                                j++
                                if(j==operatorsList.size){
                                    break
                                }
                            }
                            param2 = mparam1
                            param1=realCalculate(param1,operator,param2)
                            i = j-1
                        }
                    }
                    i++
                    if(i==operatorsList.size){
                        //遍历结束
                        break
                    }
                }
            }
            //显示对应结果
            result_textView.text = String.format("%.2f",param1)
        }else{
            result_textView.text = "0"
        }
    }

    private fun realCalculate(param1:Float,operator:String,param2:Float):Float{
        var result : Float = 0.0f
        when(operator){
            "+" ->{
                result = param1 + param2
            }
            "-" ->{
                result = param1 - param2
            }
            "×" ->{
                result = param1 * param2
            }
            "÷" ->{
                result = param1 / param2
            }
        }
        return result
    }
}