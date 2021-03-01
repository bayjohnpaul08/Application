package com.example.application;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ManufacturersFragment extends Fragment implements Adapter.OnclickInterface {

    DbHelper db;
    RecyclerView recyclerView;
    Adapter adapter;
    ArrayList <ManufacturersHelperClass> manufacturersList = new ArrayList<>();
    TextView noData;
    ImageView noImage;


    public ManufacturersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_manufacturers, container, false);
       db = new DbHelper(getActivity());

       noData = (TextView) view.findViewById(R.id.noData);
       noImage = (ImageView) view.findViewById(R.id.emptyImage);

           // SET THE MANUFACTURERS LIST IN RECYCLERVIEW
           recyclerView = (RecyclerView) view.findViewById(R.id.view);
           RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
           recyclerView.setLayoutManager(mLayoutManager);
           recyclerView.setItemAnimator(new DefaultItemAnimator());
           recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),LinearLayoutManager.VERTICAL));

       // read all data
       readData();

       return view;
    }


    private void readData() {
        Cursor cursor = db.readAllData();

        if(cursor.getCount() == 0){
            noData.setVisibility(View.VISIBLE);
            noImage.setVisibility(View.VISIBLE);
        }

        while (cursor.moveToNext()){

            noData.setVisibility(View.GONE);
            noImage.setVisibility(View.GONE);

            ManufacturersHelperClass obj = new ManufacturersHelperClass(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4));
            manufacturersList.add(obj);
        }
        adapter = new Adapter(ManufacturersHelperClass.DIFF_CALLBACK,this );
        adapter.submitList(manufacturersList);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onView(int position) {

        Bundle bundle = new Bundle();
        bundle.putParcelable("data", manufacturersList.get(position));

        FragmentManager fragmentManager =getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        ManufacturersViewFragment fragment = new ManufacturersViewFragment();
        fragment.setArguments(bundle);

        fragmentTransaction.replace(R.id.container_fragment, fragment).addToBackStack(null);
        fragmentTransaction.commit();

    }

    @Override
    public void onEdit(int position) {

        Bundle bundle = new Bundle();
        bundle.putParcelable("data", manufacturersList.get(position));

        FragmentManager fragmentManager =getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        ManufacturersEditFragment fragment = new ManufacturersEditFragment();
        fragment.setArguments(bundle);

        fragmentTransaction.replace(R.id.container_fragment, fragment).addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onDelete(int position) {
        warningDialog(position);
    }

    private void warningDialog(int position) {

        String id = manufacturersList.get(position).getId();
        String name = manufacturersList.get(position).getManufacturersName();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you want to delete " + name + "?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Integer delete = db.deleteOneRow(id);

                if(delete > 0){
                    Toast.makeText(getActivity(), name + " Data Successfully Deleted.", Toast.LENGTH_LONG).show();

                    FragmentManager fragmentManager = getParentFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    ManufacturersFragment fragment = new ManufacturersFragment();
                    fragmentTransaction.replace(R.id.container_fragment, fragment).addToBackStack(null);
                    fragmentTransaction.commit();

                } else{
                    Toast.makeText(getActivity(), name + " Failed to Delete.", Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }
}