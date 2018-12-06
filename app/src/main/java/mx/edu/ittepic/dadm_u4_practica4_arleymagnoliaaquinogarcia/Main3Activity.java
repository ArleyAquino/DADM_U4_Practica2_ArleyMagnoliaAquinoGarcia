package mx.edu.ittepic.dadm_u4_practica4_arleymagnoliaaquinogarcia;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Main3Activity extends AppCompatActivity {
Button inmueble, propietario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        inmueble=findViewById(R.id.inmueble);
        propietario=findViewById(R.id.propietario);

        inmueble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent otra = new Intent(Main3Activity.this, Main2Activity.class);
                startActivity(otra);
            }
        });
        propietario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent otra = new Intent(Main3Activity.this, MainActivity.class);
                startActivity(otra);
            }
        });
    }
}
