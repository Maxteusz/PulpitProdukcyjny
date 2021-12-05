package com.example.pulpitprodukcyjny;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.CONNECTIVITY_SERVICE;


public class LogoFragment extends Fragment {
    ImageView logoImage;
    ArrayList<Order> orders;
    int repeatGetHeadersSpecialPole = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_logo, parent, false);

    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        logoImage = view.findViewById(R.id.logo_view);
        ObjectAnimator fade_in = ObjectAnimator.ofFloat(logoImage, "alpha", 0f, 1f);

        fade_in.setDuration(1500);

        fade_in.start();
        fade_in.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                getHeadersSpecialPole();
                getOrders();


            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }


    public  ArrayList<Order> getOrders() {
        orders = new ArrayList<>();
        if (isWifiConnecnted()) {
            int socketTimeout = 8000;
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            RetryPolicy _policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            RequestQueue queue = Volley.newRequestQueue(getContext());
            String url = "http://" + MainActivity.connectionIpAdress + ":" + MainActivity.connectionPort +"/PobierzZlecenia";

            ArrayList<Order> finalOrders = orders;
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
                            Boolean inProgrss = order.getBoolean("Wtoku");
                            JSONArray temp = order.getJSONArray("ListaZasobow");
                            if(temp.length() > 0) {
                                for (int j = 0; j < temp.length(); j++) {
                                    JSONObject resource = (JSONObject) temp.get(j);
                                    String resourceName = resource.getString("nazwa");
                                    int plannedQuantity = resource.getInt("iloscZaplanowana");
                                    int realizedQuantity = resource.getInt("iloscZrealizowana");
                                    int plannedTime = resource.getInt("CzasPlanowany");
                                    int realTime = resource.getInt("CzasRzeczywisty");
                                    resources.add(new Resource(resourceName, plannedQuantity, realizedQuantity,plannedTime,realTime));
                                }
                                Log.i("Pobrano zasoby dla zlecenia " + name, "Liczba zasbów " + temp.length());
                            }
                            finalOrders.add(new Order(id, name, numberRelatedDocument, product, productCatalogNumber, pole1, pole2, pole3, shipDate, resources, inProgrss));
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }

                    Log.i("Pobrano zlecenie", "Pobrano  zleceń :" + finalOrders.size());
                    TasksFragment tasksFragment = new TasksFragment();
                    openFragment(tasksFragment);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("orders", finalOrders);
                    tasksFragment.setArguments(bundle);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.animator.slide_in, R.animator.slide_out, R.animator.slide_in, R.animator.slide_out)
                            .replace(R.id.your_placeholder, new FailedConnectionFragment())
                            .commit();
                }
            });

            req.setRetryPolicy(_policy);
            queue.add(req);
            return finalOrders;

        } else openFragment(new FailedConnectionFragment());
        return orders;
    }

    public boolean isWifiConnecnted() {
        boolean connected = false;
        ConnectivityManager connManager = (ConnectivityManager) getContext().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWifi.isConnected())
            connected = true;
        return connected;

    }

    public void openFragment(Fragment fragment) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.slide_in, R.animator.slide_out, R.animator.slide_in, R.animator.slide_out)
                .replace(R.id.your_placeholder, fragment)
                .commit();
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




}
