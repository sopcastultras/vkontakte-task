package com.atdroid.atyurin.futuremoney.fragments;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.atdroid.atyurin.futuremoney.R;
import com.atdroid.atyurin.futuremoney.dao.AccountsDAO;
import com.atdroid.atyurin.futuremoney.dao.IncomesDAO;
import com.atdroid.atyurin.futuremoney.dao.OutcomesDAO;
import com.atdroid.atyurin.futuremoney.serialization.Account;
import com.atdroid.atyurin.futuremoney.serialization.Income;
import com.atdroid.atyurin.futuremoney.serialization.Outcome;
import com.atdroid.atyurin.futuremoney.serialization.Total;
import com.atdroid.atyurin.futuremoney.utils.DateFormater;
import com.atdroid.atyurin.futuremoney.utils.KeyboardManager;
import com.atdroid.atyurin.futuremoney.utils.TotalsCalculator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by atdroid on 14.09.2015.
 *
 * elements description
 * 0 - name (EditText)
 * 1 - amount (EditText)
 * 2 - type (LinearLayout{Text, Spinner})
 * 3 - single date (Date Picker)
 * 4 - begin date (Date Picker)
 * 5 - end date (Date Picker)
 * 6 - period (LinearLayout{Text, Spinner, EditText(optional - depende on spinner)})
 *
 */

public class TotalsFragment extends Fragment {
    final static String INCOME_KEY = "key_total";
    final static String LOG_TAG = "TotalsFragment";
    Total total;
    View rootView;
    Spinner spType;
    TextView tvCalculateDateValue, tvBeginDateValue, tvAccountsTotalValue, tvOutcomesTotalValue, tvIncomesTotalValue, tvTotalValue;
    ArrayAdapter<String> adapterType;
    LinearLayout llCalculateDate,llTotalsLayout;
    RelativeLayout llBeginDate;
    Activity activity;
    FragmentManager fragmentManager;

    public static TotalsFragment newInstance(Activity activity, FragmentManager fragmentManager) {
        TotalsFragment totalsFragment = new TotalsFragment();
        totalsFragment.activity = activity;
        totalsFragment.fragmentManager = fragmentManager;
        totalsFragment.total = new Total();
        return totalsFragment;
    }

    public TotalsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);//switch off menu for fragment
        new KeyboardManager(this).closeKeyboard();
        rootView =  inflater.inflate(R.layout.fragment_totals, container, false);
        //begin date type
        spType = (Spinner) rootView.findViewById(R.id.sp_begin_date_type);
        adapterType = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.total_begin_types));
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(adapterType);
        spType.setSelection(total.getBeginDateType());
        adapterType.notifyDataSetChanged();
        spType.setOnItemSelectedListener(typeSelectedListener);
        //calculate date
        llBeginDate = (RelativeLayout) rootView.findViewById(R.id.ll_begin_date);
        llBeginDate.setOnClickListener(beginDateListener);
        SimpleDateFormat sdf = new SimpleDateFormat(DateFormater.DATE_FORMAT);
        tvBeginDateValue = (TextView) rootView.findViewById(R.id.tv_begin_date_value);
        tvBeginDateValue.setText(sdf.format(total.getBegin_date().getTime()));

        //calculate date
        llCalculateDate = (LinearLayout) rootView.findViewById(R.id.ll_calculate_date);
        llCalculateDate.setOnClickListener(endDateListener);
        tvCalculateDateValue = (TextView) rootView.findViewById(R.id.tv_calculate_date_value);
        tvCalculateDateValue.setText(sdf.format(total.getEnd_date().getTime()));

        Button btnCalculate = (Button) rootView.findViewById(R.id.btn_calc_totals);
        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calcTotals();
            }
        });
        return rootView;

    }

    private AdapterView.OnItemSelectedListener typeSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            Log.d("typeSelectedListener", "" + position);
            if (position == Total.TYPE_ALL){
                total.setBeginDateType(Total.TYPE_ALL);
                llBeginDate.setVisibility(View.GONE);
            }else if (position == Total.TYPE_SELECTED_DATE) {
                total.setBeginDateType(Total.TYPE_SELECTED_DATE);
                SimpleDateFormat sdf = new SimpleDateFormat(DateFormater.DATE_FORMAT);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                tvBeginDateValue.setText(sdf.format(calendar.getTime()));
                llBeginDate.setVisibility(View.VISIBLE);
                tvBeginDateValue.setText(sdf.format(calendar.getTime()));
                total.setBegin_date(calendar);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };
    private View.OnClickListener beginDateListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DatePickerFragment datePicker = new DatePickerFragment() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    SimpleDateFormat sdf = new SimpleDateFormat(DateFormater.DATE_FORMAT);
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, day);
                    tvBeginDateValue.setText(sdf.format(calendar.getTime()));
                    total.setBegin_date(calendar);
                }
            };
            datePicker.setCalendar(total.getBegin_date());
            datePicker.show(getFragmentManager(), "datePicker");
        }
    };
    private View.OnClickListener endDateListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DatePickerFragment datePicker = new DatePickerFragment() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    SimpleDateFormat sdf = new SimpleDateFormat(DateFormater.DATE_FORMAT);
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, day);
                    tvCalculateDateValue.setText(sdf.format(calendar.getTime()));
                    total.setEnd_date(calendar);
                }
            };
            datePicker.setCalendar(total.getEnd_date());
            datePicker.show(getFragmentManager(), "datePicker");
//            new CloseKeyboard().execute(datePicker);

        }
    };
    public void calcTotals(){
        new CalculateTotalsTask(getActivity()).execute();
    }

    public class CalculateTotalsTask extends AsyncTask<Void,Void,Void> {
        ProgressDialog pd;

        public CalculateTotalsTask(Activity activity) {
            this.pd = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(LOG_TAG, "CalculateTotalsTask");
            pd.setMessage("Start");
            pd.show();
        }
        @Override
        protected Void doInBackground(Void... params) {
            Calendar begin = total.getBegin_date();
            Calendar end= total.getEnd_date();
            //incomes
            IncomesDAO incomesDAO = new IncomesDAO(getActivity().getBaseContext());
            incomesDAO.openReadable();
            ArrayList<Income> singleIncomes = incomesDAO.getIncomesInPeriodWithType(Income.TYPE_SINGLE, total.getBegin_date(), total.getEnd_date());
            ArrayList<Income> periodicalIncomes = incomesDAO.getIncomesInPeriodWithType(Income.TYPE_PERIODICAL, total.getBegin_date(), total.getEnd_date());
            incomesDAO.close();

            OutcomesDAO outcomesDAO = new OutcomesDAO(getActivity().getBaseContext());
            outcomesDAO.openReadable();
            ArrayList<Outcome> singleOutcomes = outcomesDAO.getOutcomesInPeriodWithType(Outcome.TYPE_SINGLE, total.getBegin_date(), total.getEnd_date());
            ArrayList<Outcome> periodicalOutcomes = outcomesDAO.getOutcomesInPeriodWithType(Outcome.TYPE_PERIODICAL, total.getBegin_date(), total.getEnd_date());
            incomesDAO.close();

            AccountsDAO accountsDAO = new AccountsDAO(getActivity().getBaseContext());
            accountsDAO.openReadable();
            ArrayList<Account> accounts = accountsDAO.getAllAccounts();
            accountsDAO.close();

            TotalsCalculator totalsCalc = new TotalsCalculator(total);
            totalsCalc.setSingleIncomes(singleIncomes);
            totalsCalc.setPeriodicalIncomes(periodicalIncomes);
            totalsCalc.setSingleOutcomes(singleOutcomes);
            totalsCalc.setPeriodicalOutcomes(periodicalOutcomes);
            totalsCalc.setAccounts(accounts);
            totalsCalc.calculateTotals();
            total.setAccountsAmount(totalsCalc.getAccountsAmount());
            total.setIncomeAmount(totalsCalc.getIncomesAmount());
            total.setOutcomeAmount(totalsCalc.getOutcomesAmount());
            total.setTotalAmount(totalsCalc.getTotalAmount());
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            llTotalsLayout = (LinearLayout) rootView.findViewById(R.id.ll_totals_values);
            tvAccountsTotalValue = (TextView) rootView.findViewById(R.id.tv_accounts_total_value);
            tvAccountsTotalValue.setText(Double.toString(total.getAccountsAmount()));
            tvIncomesTotalValue = (TextView) rootView.findViewById(R.id.tv_incomes_total_value);
            tvIncomesTotalValue.setText(Double.toString(total.getIncomeAmount()));
            tvOutcomesTotalValue = (TextView) rootView.findViewById(R.id.tv_outcomes_total_value);
            tvOutcomesTotalValue.setText(Double.toString(total.getOutcomeAmount()));
            tvTotalValue = (TextView) rootView.findViewById(R.id.tv_total_value);
            if (total.getTotalAmount() > 0){
                tvTotalValue.setTextColor(getResources().getColor(R.color.income_item_value));
            }else {
                tvTotalValue.setTextColor(getResources().getColor(R.color.outcome_item_value));
            }
            tvTotalValue.setText(Double.toString(total.getTotalAmount()));
            llTotalsLayout.setVisibility(View.VISIBLE);
            if (pd.isShowing()) {
                pd.dismiss();
            }
            Log.d(LOG_TAG, "Total Amt: " + Double.toString(total.getTotalAmount()));
        }
    }

  /*  public class CloseKeyboard extends AsyncTask<DatePickerFragment,DatePickerFragment,DatePickerFragment>{

        @Override
        protected DatePickerFragment doInBackground(DatePickerFragment... params) {
            DatePickerFragment dbf = params[0];
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return dbf;
        }
        @Override
        protected void onPostExecute(DatePickerFragment dbf){
            super.onPostExecute(dbf);
            Activity activity;
            activity = getActivity();
            Log.d(LOG_TAG, "CloseKeyboard - onPostExecute");
            View view = activity
                    .getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            boolean b = dbf.isVisible();
            b = !b;
        }
    }*/


}