package com.mentorandroid.beerorcoffee;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.mentorandroid.beerorcoffee.model.Local;
import com.mentorandroid.beerorcoffee.model.Resposta;
import com.mentorandroid.beerorcoffee.util.Util;
import com.mentorandroid.beerorcoffee.webservice.GenericRequest;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText nomeLocalidade;
    private EditText endereco;
    private EditText latitude;
    private EditText longitude;
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String knownName;
    private double lat;
    private double lng;

    private RadioGroup radioGroup;
    private RadioButton radioButton;


    private Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        nomeLocalidade = (EditText) findViewById(R.id.nomeLocalidade);
        endereco = (EditText) findViewById(R.id.endereco);
        latitude = (EditText) findViewById(R.id.latitude);
        longitude = (EditText) findViewById(R.id.longitude);
        ctx = this;

        radioGroup = (RadioGroup) findViewById(R.id.radio);
        int selectedId = radioGroup.getCheckedRadioButtonId();

        radioButton = (RadioButton) findViewById(selectedId);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Util.isNetworkAvailable(ctx)) {
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    radioButton = (RadioButton) findViewById(selectedId);
                    Integer tipoBebida = 1;
                    if(radioButton.getText().equals("Cerveja")){
                        tipoBebida = 1;
                    }else
                    if(radioButton.getText().equals("Café")){
                        tipoBebida = 2;
                    }else
                    if(radioButton.getText().equals("Ambos")){
                        tipoBebida = 3;
                    }
                    if (validaForm()) {
                        Local local = new Local();
                        local.setName(nomeLocalidade.getText().toString());
                        local.setAddress(endereco.getText().toString());
                        local.setLatitude(Double.parseDouble(latitude.getText().toString()));
                        local.setLongitude(Double.parseDouble(longitude.getText().toString()));
                        local.setBeverage(tipoBebida);
                        gravaLocal(local);
                    }
                }else{
                    Toast.makeText(ctx, "Verifique sua conexão com a internet!", Toast.LENGTH_LONG).show();
                }
            }
        });

        getLocationbyLatLng();

    }

    private boolean validaForm() {
        // Reset errors.
        nomeLocalidade.setError(null);
        endereco.setError(null);
        latitude.setError(null);
        longitude.setError(null);



        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(nomeLocalidade.getText().toString())) {
            nomeLocalidade.setError(getString(R.string.error_local));
            focusView = nomeLocalidade;
            cancel = true;
        }

        if (TextUtils.isEmpty(endereco.getText().toString())) {
            endereco.setError(getString(R.string.error_endereco));
            focusView = endereco;
            cancel = true;
        }
        if (TextUtils.isEmpty(latitude.getText().toString())) {
            latitude.setError(getString(R.string.error_lat));
            focusView = latitude;
            cancel = true;
        }
        if (TextUtils.isEmpty(longitude.getText().toString())) {
            longitude.setError(getString(R.string.error_lng));
            focusView = longitude;
            cancel = true;
        }

        if (cancel) {

            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    public void getLocationbyLatLng() {
        double[] d = fetchCurrentLocation();
        lat = d[0];
        lng = d[1];
        if(d[0] == 0.0 ){
            Toast.makeText(ctx, "Não foi possível obter sua localização", Toast.LENGTH_LONG).show();
        }else {
            latitude.setText(String.valueOf(lat));
            longitude.setText(String.valueOf(lng));
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(lat, lng, 1);
                address = addresses.get(0).getAddressLine(0);
                city = addresses.get(0).getLocality();
                state = addresses.get(0).getAdminArea();
                country = addresses.get(0).getCountryName();
                postalCode = addresses.get(0).getPostalCode();
                knownName = addresses.get(0).getFeatureName();
                String enderecoConcatenado = address + ", " + city + "," + state + "," + country;
                endereco.setText(enderecoConcatenado);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void gravaLocal(Local local) {

        Map<String, String> mHeaders = new ArrayMap<String, String>();
        mHeaders.put("Content-Type", "application/json");
        mHeaders.put("x-api-key", "IfXJnQVdjo1fI4z6OQTWB6RPJ8Qs4JbcaDOZ83vt");
        RequestQueue queue = Volley.newRequestQueue(ctx);
        GenericRequest<Resposta> myReq = new GenericRequest<Resposta>(Request.Method.POST, "https://c7q5vyiew7.execute-api.us-east-1.amazonaws.com/prod/places",
                Resposta.class, local, createMyReqSuccessListener(), createMyReqErrorListener(), mHeaders);
        Log.d("DEBUG", "Entrou no response");
        queue.add(myReq);
    }

    private Response.Listener<Resposta> createMyReqSuccessListener() {
        return new Response.Listener<Resposta>() {
            @Override
            public void onResponse(Resposta response) {
                Toast.makeText(ctx, response.getMessage().toString(), Toast.LENGTH_LONG).show();
                Toast.makeText(ctx, response.getPlace_id().toString(), Toast.LENGTH_LONG).show();
                nomeLocalidade.setText("");
                endereco.setText("");
                latitude.setText("");
                longitude.setText("");
            }
        };
    }


    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Do whatever you want to do with error.getMessage();

                Log.d("DEBUG", "Entrou no ERRO");
                Toast.makeText(ctx, "Ocorreu algum erro! Tente novamente em alguns minutos!", Toast.LENGTH_LONG).show();
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private double[] fetchCurrentLocation() {
        double[] d = getlocation();
        return d != null && d.length > 0 && d[0] != 0.0 ? d : new double[]{lat, lng};
    }


    public double[] getlocation() {

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = lm.getProviders(true);
        Location l = null;
        for (int i = 0; i < providers.size(); i++) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                //requestPermissions(LOCATION_PERMS, LOCATION_REQUEST);
                Toast.makeText(ctx, "Você deve abiliar a permissão de localização!", Toast.LENGTH_LONG).show();
            }else {
                l = lm.getLastKnownLocation(providers.get(i));
            }
            if (l != null)
                break;
        }
        double[] gps = new double[2];
        if (l != null) {
            gps[0] = l.getLatitude();
            gps[1] = l.getLongitude();
        }
        return gps;

    }

}
