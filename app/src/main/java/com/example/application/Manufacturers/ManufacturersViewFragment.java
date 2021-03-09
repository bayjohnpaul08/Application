package com.example.application.Manufacturers;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.application.DbHelper;
import com.example.application.Manufacturers.ManufacturersFragment;
import com.example.application.Manufacturers.ManufacturersHelperClass;
import com.example.application.Products.ProductAdapter;
import com.example.application.Products.ProductEditFragment;
import com.example.application.Products.ProductModel;
import com.example.application.Products.ProductViewFragment;
import com.example.application.Products.ProductsFragment;
import com.example.application.R;
import com.example.application.RelatedAdapter;
import com.example.application.RelatedProductsHelperClass;

import java.util.ArrayList;

public class ManufacturersViewFragment extends Fragment implements ProductAdapter.OnclickInterface {

    private ImageView viewImage;
    private TextView name;
    private TextView created;
    private TextView modified;
    private Button back;
    private RecyclerView relatedProducts;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    ArrayList<ProductModel> productList = new ArrayList<>();
    DbHelper db;
    ProductAdapter adapter;

    public ManufacturersViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manufacturers_view, container, false);

        db = new DbHelper(getActivity());
        viewImage = (ImageView) view.findViewById(R.id.viewImage);
        name = (TextView) view.findViewById(R.id.name);
        created = (TextView) view.findViewById(R.id.created);
        modified = (TextView) view.findViewById(R.id.modified);
        relatedProducts = (RecyclerView) view.findViewById(R.id.relatedProductsRecyclerView);
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

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        relatedProducts.setLayoutManager(mLayoutManager);
        relatedProducts.setItemAnimator(new DefaultItemAnimator());
        relatedProducts.addItemDecoration(new DividerItemDecoration(getActivity(),LinearLayoutManager.VERTICAL));

        Bundle bundle = getArguments();
        ManufacturersHelperClass manufacturersHelperClass = bundle.getParcelable("data");

        related(manufacturersHelperClass);

        name.setText(manufacturersHelperClass.getManufacturersName());
        created.setText(manufacturersHelperClass.getManufacturersCreated());
        modified.setText(manufacturersHelperClass.getManufacturersModified());
        Glide.with(getContext()).load(manufacturersHelperClass.getManufacturersImage()).placeholder(R.drawable.noimage).into(viewImage);

        return view;
    }

    private void related(ManufacturersHelperClass manufacturersHelperClass) {
        productList = db.readAllRelated(manufacturersHelperClass);

        adapter = new ProductAdapter(ProductModel.DIFF_CALLBACK, this);
        adapter.submitList(productList);
        relatedProducts.setAdapter(adapter);

    }

    @Override
    public void onProductView(int position) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("data", productList.get(position));

        FragmentManager fragmentManager =getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        ProductViewFragment fragment = new ProductViewFragment();
        fragment.setArguments(bundle);

        fragmentTransaction.replace(R.id.container_fragment, fragment).addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onProductEdit(int position) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("data", productList.get(position));

        FragmentManager fragmentManager =getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        ProductEditFragment fragment = new ProductEditFragment();
        fragment.setArguments(bundle);

        fragmentTransaction.replace(R.id.container_fragment, fragment).addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onProductDelete(int position) {
        warningDialog(position);
    }

    private void warningDialog(int position) {

        String id = productList.get(position).getProductId();
        String name = productList.get(position).getProductName();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you want to delete " + name + "?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Integer delete = db.deleteOneProduct(id);

                if(delete > 0){
                    Toast.makeText(getActivity(), name + " Data Successfully Deleted.", Toast.LENGTH_LONG).show();

                    FragmentManager fragmentManager = getParentFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    ProductsFragment fragment = new ProductsFragment();
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