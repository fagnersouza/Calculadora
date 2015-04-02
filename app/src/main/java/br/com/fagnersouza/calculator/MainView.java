package br.com.fagnersouza.calculator;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;


public class MainView extends ActionBarActivity {
    private Map<Integer, View> viewCache = new HashMap<Integer,View>();
    private boolean point;
    private OPERATION operation;
    private boolean signal = true;
    private String lastResult;
    private StringBuilder currentTerm;
    private boolean formatComma;

    enum OPERATION{
        SUM("+"),
        SUBTRACTION("-"),
        PRODUCT("x"),
        RATIO("/"),
        PERCENTAGE("%");
        String operator;

        OPERATION(String operator){
            this.operator = operator;
        }

        public String toString(){
            return this.operator;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);

        loadViewCache();
        erase(null);
    }

    private void loadViewCache(){
        //Buttons
        viewCache.put(R.id.bt0,findViewById(R.id.bt1));
        viewCache.put(R.id.bt0,findViewById(R.id.bt2));
        viewCache.put(R.id.bt0,findViewById(R.id.bt3));
        viewCache.put(R.id.bt0,findViewById(R.id.bt4));
        viewCache.put(R.id.bt0,findViewById(R.id.bt5));
        viewCache.put(R.id.bt0,findViewById(R.id.bt6));
        viewCache.put(R.id.bt0,findViewById(R.id.bt7));
        viewCache.put(R.id.bt0,findViewById(R.id.bt8));
        viewCache.put(R.id.bt0,findViewById(R.id.bt9));
        viewCache.put(R.id.bt0,findViewById(R.id.bt0));

        //TextView
        viewCache.put(R.id.txtOutput,findViewById(R.id.txtOutput));
        viewCache.put(R.id.txtResult,findViewById(R.id.txtResult));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        /*
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
        */

        return false;
    }

    public void typeDigit(View view){
        TextView text = (TextView)viewCache.get(R.id.txtOutput);
        String digit = "";
        switch (view.getId()){
            case R.id.bt1:digit="1";break;
            case R.id.bt2:digit="2";break;
            case R.id.bt3:digit="3";break;
            case R.id.bt4:digit="4";break;
            case R.id.bt5:digit="5";break;
            case R.id.bt6:digit="6";break;
            case R.id.bt7:digit="7";break;
            case R.id.bt8:digit="8";break;
            case R.id.bt9:digit="9";break;
            case R.id.bt0:digit="0";break;
            default:
        }

        currentTerm.append(digit);
        text.append(digit);
    }

    public void erase(View view){
        TextView text = (TextView)viewCache.get(R.id.txtOutput);
        text.setText("");

        text = (TextView) viewCache.get(R.id.txtResult);
        text.setText("");

        point = false;
        operation = null;
        signal = true;
        lastResult = "";
        currentTerm = new StringBuilder();
        formatComma = false;
    }


    public void punctuate(View view){
        formatComma = true;
        if(!point) {
            TextView text = (TextView) viewCache.get(R.id.txtOutput);
            text.setText(text.getText()+",");
            currentTerm.append(",");
            point = true;
        }
    }

    public void multiply(View view){
        process(OPERATION.PRODUCT);
    }

    public void divide(View view){
        process(OPERATION.RATIO);
    }

    public void subtract(View view){
        process(OPERATION.SUBTRACTION);
    }

    public void sum(View view){
        process(OPERATION.SUM);
    }

    private void process(OPERATION target){
        TextView text = (TextView) viewCache.get(R.id.txtOutput);

        if(text.length() == 0)
            return;

        if(operation == null){
            text.setText(text.getText()+target.toString());
        }else {
            String sum = "";
            sum = text.getText().toString();
            text.setText(sum+target.toString());

            if(lastResult.length() == 0)
                lastResult = formatResult(calculate(sum));
            else
                lastResult = formatResult(calculate(lastResult + operation + currentTerm));

            text = (TextView) viewCache.get(R.id.txtResult);
            text.setText("="+ lastResult);
        }

        operation = target;
        currentTerm = new StringBuilder();
        point = false;
    }

    private String formatResult(Double value){
        String pattern;

        if(formatComma)
            pattern = "#.##";
        else
            pattern = "#";

        DecimalFormat df = new DecimalFormat(pattern);

        return df.format(value);
    }

    private String brackets(String data){
        return "(" + data + ")";
    }

    private Double calculate(String dado){
        dado = dado.replace(",",".");
        String signal = "";

        if(dado.startsWith("+") || dado.startsWith("-")) {
            signal = dado.substring(0,1);
            dado = dado.substring(1);
        }

        String [] terms = dado.split("[-|+|x|/]");

        if(dado.contains("-"))
            return Double.valueOf(signal+terms[0]) - Double.valueOf(terms[1]);

        if(dado.contains("+"))
            return Double.valueOf(signal+terms[0]) + Double.valueOf(terms[1]);

        if(dado.contains("x"))
            return Double.valueOf(signal+terms[0]) * Double.valueOf(terms[1]);

        if(dado.contains("/"))
            return Double.valueOf(signal+terms[0]) / Double.valueOf(terms[1]);

        return 0.0;
    }

    public void percentage(View view){

    }

    public void result(View view){
        TextView result = (TextView) viewCache.get(R.id.txtResult);

        if(lastResult.length() == 0) {
            String sum = "";
            TextView output = (TextView) viewCache.get(R.id.txtOutput);

            sum = output.getText().toString();
            output.setText(sum);

            lastResult = formatResult(calculate(sum));
        }else {
            if(operation != null && currentTerm.length() != 0)
                lastResult = formatResult(calculate(lastResult + operation + currentTerm));
        }

        result.setText("="+lastResult);
        currentTerm = new StringBuilder();
        operation = null;
    }

    public void changeSignal(View view){
        if(signal){
            signal = false;
            TextView text = (TextView) viewCache.get(R.id.txtOutput);
            text.setText("-"+text.getText());
        }else{
            signal = true;
            TextView text = (TextView) viewCache.get(R.id.txtOutput);
            text.setText(text.getText().toString().substring(1));
        }
    }
}
