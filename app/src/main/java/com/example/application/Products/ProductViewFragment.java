package com.example.application.Products;

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
import com.example.application.Manufacturers.ManufacturersFragment;
import com.example.application.R;

public class ProductViewFragment extends Fragment {

    private ImageView productViewImage;
    private TextView productViewName;
    private TextView productViewPrice;
    private TextView productViewManufacturer;
    private TextView productViewCreated;
    private TextView productViewModified;
    private Button productBack;

    public ProductViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_product_view, container, false);

        productViewImage = (ImageView) view.findViewById(R.id.productViewImage);
        productViewName = (TextView) view.findViewById(R.id.prodName);
        productViewPrice = (TextView) view.findViewById(R.id.prodPrice);
        productViewManufacturer = (TextView) view.findViewById(R.id.prodManufacturer);
        productViewCreated = (TextView) view.findViewById(R.id.prodCreated);
        productViewModified = (TextView) view.findViewById(R.id.prodModified);
        productBack = (Button) view.findViewById(R.id.productBack);

        productBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                ProductsFragment fragment = new ProductsFragment();

                fragmentTransaction.replace(R.id.container_fragment, fragment);
                fragmentTransaction.commit();
            }
        });

        Bundle bundle = getArguments();
        ProductModel productModel = bundle.getParcelable("data");

        productViewName.setText(productModel.getProductName());
        productViewPrice.setText(productModel.getProductPrice());
        productViewManufacturer.setText(productModel.getProductManufacturer());
        productViewModified.setText(productModel.getProductModified());
        productViewCreated.setText(productModel.getProductCreated());
        Glide.with(getContext()).load(productModel.getProductImage()).placeholder(R.drawable.noimage).into(productViewImage);

        return view;
    }
}