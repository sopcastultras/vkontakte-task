package com.atdroid.atyurin.futuremoney.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.atdroid.atyurin.futuremoney.R;
import com.atdroid.atyurin.futuremoney.adapters.AccountsAdapter;
import com.atdroid.atyurin.futuremoney.dao.AccountsDAO;
import com.atdroid.atyurin.futuremoney.serialization.Account;

import java.util.ArrayList;

/**
 * Created by atdroid on 14.09.2015.
 */

public class AccountsFragment extends Fragment {
    Activity activity;
    FragmentManager fragmentManager;
    ArrayList<Account> accounts;
    AccountsAdapter adapter;
    AccountsDAO dao;
    final static String LOG_TAG = "AccountsFragment";
    public static AccountsFragment newInstance(Activity activity, FragmentManager fragmentManager) {
        AccountsFragment accountsFragment = new AccountsFragment();
        accountsFragment.activity = activity;
        accountsFragment.fragmentManager = fragmentManager;
        return accountsFragment;
    }

    public AccountsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);//switch on menu for fragment
        View rootView;
        rootView = inflater.inflate(R.layout.fragment_budget_items, container, false);
        ListView lvAccounts = (ListView) rootView.findViewById(R.id.lv_budget_items);
        dao = new AccountsDAO(activity.getBaseContext());
        dao.openReadable();
        accounts = dao.getAllAccounts();
        dao.close();
        adapter = new AccountsAdapter(activity.getBaseContext(), accounts);
        lvAccounts.setOnItemClickListener(itemClickListener);
        lvAccounts.setAdapter(adapter);
        registerForContextMenu(lvAccounts);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAccount();
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_SHORT)
//                        .setAction("Action", null).show();
            }
        });
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.budget_item, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_btn_add_item) {
            addAccount();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addAccount(){
        fragmentManager.beginTransaction()
                .replace(R.id.container, AccountItemFragment.newInstance())
                .commit();
    }

    public void updateAccount(Account account){
        fragmentManager.beginTransaction()
                .replace(R.id.container, AccountItemFragment.newInstance(account))
                .commit();
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.lv_budget_items) {
            MenuInflater inflater = activity.getMenuInflater();
            inflater.inflate(R.menu.menu_budget_item_list_context, menu);
        }
    }

    public AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            updateAccount(accounts.get(position));
        }
    };

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.menu_delete_budget_item:
                Log.d(LOG_TAG, accounts.get(info.position).getName());
                dao.openWritable();
                dao.deleteAccount(accounts.get(info.position));
                dao.close();
                accounts.remove(info.position);
                adapter.notifyDataSetChanged();
                Toast.makeText(getActivity().getBaseContext(), getResources().getString(R.string.msg_budget_item_success), Toast.LENGTH_LONG).show();

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
