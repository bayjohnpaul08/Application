package com.example.application;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

public class ManufacturersViewFragment extends Fragment {

    private ImageView viewImage;
    private TextView name;
    private TextView created;
    private TextView modified;
    private Button back;
    ManufacturersHelperClass manufacturersHelperClass;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    public ManufacturersViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manufacturers_view, container, false);

        viewImage = (ImageView) view.findViewById(R.id.viewImage);
        name = (TextView) view.findViewById(R.id.name);
        created = (TextView) view.findViewById(R.id.created);
        modified = (TextView) view.findViewById(R.id.modified);
        back = (Button) view.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager = getParentFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();

                ManufacturersFragment fragment = new ManufacturersFragment();

                fragmentTransaction.replace(R.id.container_fragment, fragment);
                fragmentTransaction.commit();
            }
        });

        Bundle bundle = getArguments();
        manufacturersHelperClass = bundle.getParcelable("data");

        name.setText(manufacturersHelperClass.getManufacturersName());
        created.setText(manufacturersHelperClass.getManufacturersCreated());
        modified.setText(manufacturersHelperClass.getManufacturersModified());
        Glide.with(getContext()).load(manufacturersHelperClass.getManufacturersImage()).placeholder(R.drawable.noimage).into(viewImage);

        return view;
    }
}