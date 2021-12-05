package com.example.pulpitprodukcyjny;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class TasksFragment extends Fragment {

    RecyclerView recyclerView;
    TaskAdapter taskAdapter;
    ArrayList<Order> orders;
    Handler handler;
    FloatingActionButton floatingSortButton;
    Dialog dialog;
    RadioButton radioButton1, radioButton2, radioButton3, radioButton4;
    MaterialButton setSortButton;
    int setSortOption = 0;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        orders = (ArrayList<Order>) getArguments().get("orders");
        taskAdapter = new TaskAdapter(getContext(), orders);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_main, parent, false);

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {


        floatingSortButton = view.findViewById(R.id.testSort_button);
        floatingSortButton.setOnClickListener(v -> {
            dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.sort_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(true);
            setSortOption = 1;
            radioButton1 = dialog.findViewById(R.id.radioButton);
            radioButton1.setChecked(true);
            radioButton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSortOption = 1;
                }
            });
            radioButton2 = dialog.findViewById(R.id.radioButton2);
            radioButton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSortOption = 2;
                }
            });
            radioButton3 = dialog.findViewById(R.id.radioButton3);
            radioButton3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSortOption = 3;
                }
            });
            radioButton4 = dialog.findViewById(R.id.radioButton4);
            radioButton4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSortOption = 4;
                }
            });
            setSortButton = dialog.findViewById(R.id.sort_button);
            setSortButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    sortBy(setSortOption);

                    dialog.dismiss();

                }
            });

            dialog.show();

        });
        Log.i("Pobrano " + orders.size() + "zamówień", "");
        postponeEnterTransition();
        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                recyclerView = view.findViewById(R.id.recycler_view);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(taskAdapter);
                recyclerView.setItemViewCacheSize(150);
                startPostponedEnterTransition();
                return true;
            }
        });
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(Color.RED);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            getOrders();
            getHeadersSpecialPole();
        });

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    public void sortByOverallRealizationAsc() {
        Collections.sort(orders, new Comparator<Order>() {
            public int compare(Order obj1, Order obj2) {
                return Integer.valueOf(obj1.getOverallResult()).compareTo(obj2.getOverallResult());
            }
        });

    }

    public void sortByOverallRealizationDsc() {
        Collections.sort(orders, (obj1, obj2) -> Integer.valueOf(obj2.getOverallResult()).compareTo(obj1.getOverallResult()));

    }

    public void sortByShipDateDsc() {
        Collections.sort(orders, (obj1, obj2) -> obj2.getShipDate().compareTo(obj1.getShipDate()));


    }

    public void sortByShipDateAsc() {
        Collections.sort(orders, (obj1, obj2) -> obj1.getShipDate().compareTo(obj2.getShipDate()));

    }

    public void getOrders() {

        if (isWifiConnecnted()) {
            orders.clear();
            int socketTimeout = 8000;
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            RetryPolicy _policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            RequestQueue queue = Volley.newRequestQueue(getContext());
            String url = "http://" + MainActivity.connectionIpAdress + ":" + MainActivity.connectionPort + "/PobierzZlecenia";

            JsonArrayRequest req = new JsonArrayRequest(Request.Method.POST, url, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            ArrayList<Resource> resources = new ArrayList<>();
                            JSONObject order = (JSONObject) response.get(i);

                            int id = order.getInt("CznId");
                            String name = order.getString("Czn_NumerPelny");
                            String numberRelatedDocument = order.getString("Kontrahent");
                            String product = order.getString("Wyrob");
                            String productCatalogNumber = order.getString("NumerKatalogowy");
                            String pole1 = order.getString("PoleSpecjalne1");
                            String pole2 = order.getString("PoleSpecjalne2");
                            String pole3 = order.getString("PoleSpecjalne3");
                            String shipDate = order.getString("DataRealizacji");
                            boolean inProgrss = order.getBoolean("Wtoku");
                            JSONArray temp = order.getJSONArray("ListaZasobow");
                            if (temp.length() > 0) {
                                for (int j = 0; j < temp.length(); j++) {
                                    JSONObject resource = (JSONObject) temp.get(j);
                                    String resourceName = resource.getString("nazwa");
                                    int plannedQuantity = resource.getInt("iloscZaplanowana");
                                    int realizedQuantity = resource.getInt("iloscZrealizowana");
                                    resources.add(new Resource(resourceName, plannedQuantity, realizedQuantity));
                                }
                                Log.i("Pobrano zasoby dla zlecenia " + name, "Liczba zasbów " + temp.length());
                            }
                            orders.add(new Order(id, name, numberRelatedDocument, product, productCatalogNumber, pole1, pole2, pole3, shipDate, resources,inProgrss));
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }

                    Toast.makeText(getContext(), "Zaktualizowano zlecenia", Toast.LENGTH_SHORT).show();
                    sortBy(setSortOption);
                    swipeRefreshLayout.setRefreshing(false);


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getContext(), "Błąd przy pobieraniu zleceń", Toast.LENGTH_SHORT).show();
                }
            });

            req.setRetryPolicy(_policy);
            queue.add(req);


        }
        else
        {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getContext(), "Błąd przy pobieraniu zleceń", Toast.LENGTH_SHORT).show();
        }

    }
    public void getHeadersSpecialPole() {


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        int socketTimeout = 8000;
        RetryPolicy _policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "http://" + MainActivity.connectionIpAdress + ":" + MainActivity.connectionPort +"/PobierzNazwyPolSpecjalnych";
        JsonArrayRequest req = new JsonArrayRequest(Request.Method.POST, url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    MainActivity.headerSpecialPole1 = response.get(0).toString();
                    MainActivity.headerSpecialPole2 = response.get(1).toString();
                    MainActivity.headerSpecialPole3 = response.get(2).toString();


                } catch (JSONException e) {
                    e.printStackTrace();


                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        });
        req.setRetryPolicy(_policy);
        queue.add(req);
    }

    public boolean isWifiConnecnted() {
        boolean connected = false;
        ConnectivityManager connManager = (ConnectivityManager) getContext().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWifi.isConnected())
            connected = true;
        return connected;


    }

    public void sortBy(int option) {
        switch (option) {
            case 1:
                sortByOverallRealizationDsc();
                break;
            case 2:
                sortByOverallRealizationAsc();
                break;
            case 3:
                sortByShipDateDsc();
                break;
            case 4:
                sortByShipDateAsc();
                break;
            case 0:
                break;
        }
        taskAdapter = new TaskAdapter(getContext(), orders);
        recyclerView.setAdapter(taskAdapter);
    }

}
