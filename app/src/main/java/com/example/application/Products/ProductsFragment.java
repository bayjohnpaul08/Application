package com.example.application.Products;

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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.application.DbHelper;
import com.example.application.Manufacturers.ManufacturersHelperClass;
import com.example.application.R;

import java.util.ArrayList;

public class ProductsFragment extends Fragment implements ProductAdapter.OnclickInterface {

    ArrayList<ProductModel> productList = new ArrayList<>();
    DbHelper productDB;
    ProductAdapter productAdapter;
    RecyclerView productView;
    TextView noData;
    ImageView noImage;

    public ProductsFragment() {
        // Required empty public constructor
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_products, container, false);

        productDB = new DbHelper(getActivity());
        productView = (RecyclerView) view.findViewById(R.id.productView);
        noData = (TextView) view.findViewById(R.id.productNoData);
        noImage = (ImageView) view.findViewById(R.id.emptyProductImage);

        // SET THE MANUFACTURERS LIST IN RECYCLERVIEW
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        productView.setLayoutManager(mLayoutManager);
        productView.setItemAnimator(new DefaultItemAnimator());
        productView.addItemDecoration(new DividerItemDecoration(getActivity(),LinearLayoutManager.VERTICAL));

        // READ ALL PRODUCT DATA
        productReadAllData();
        return view;
    }

    private void productReadAllData() {
        Cursor cursor = productDB.productAllData();

        if(cursor.getCount() == 0){
            noData.setVisibility(View.VISIBLE);
            noImage.setVisibility(View.VISIBLE);
        }

        while (cursor.moveToNext()){

            noData.setVisibility(View.GONE);
            noImage.setVisibility(View.GONE);

            ProductModel obj = new ProductModel(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(8),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6));
            productList.add(obj);
        }

        productAdapter = new ProductAdapter(ProductModel.DIFF_CALLBACK,this);
        productAdapter.submitList(productList);
        productView.setAdapter(productAdapter);
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

                Integer delete = productDB.deleteOneProduct(id);

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