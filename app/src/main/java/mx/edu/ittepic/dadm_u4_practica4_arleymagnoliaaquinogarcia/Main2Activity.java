package mx.edu.ittepic.dadm_u4_practica4_arleymagnoliaaquinogarcia;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Main2Activity extends AppCompatActivity {
EditText identificador, domicilio, precioventa, preciorenta,fechatransaccion;
    BaseDatos base;
    Spinner identifiPro;
    Button insertar, consultar, eliminar, actualizar, limpiar, b1;
    String [] idp= new String[100000];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        identificador=findViewById(R.id.idd);
        domicilio=findViewById(R.id.domicilio);
        precioventa=findViewById(R.id.precioventa);
        preciorenta=findViewById(R.id.preciorenta);
        fechatransaccion=findViewById(R.id.fecha);
        insertar=findViewById(R.id.insertar);
        consultar=findViewById(R.id.consultar);
        eliminar=findViewById(R.id.borrar);
        actualizar=findViewById(R.id.actualizar);
        identifiPro=findViewById(R.id.idp);
        limpiar=findViewById(R.id.limpiar);
        b1=findViewById(R.id.b1);


        base=new BaseDatos(this, "primera",null,1); //clase de conexion BaseDatos y la bd se llama primera
        insertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codigoInsertar();
            }
        });
        consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedirId(1);//metodo que contiene el AlerDialog
            }
        });
        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(actualizar.getText().toString().startsWith("CONFIRMAR CAMBIOS")){
                    confirmacionActualizarDatos();
                    return;
                }
                pedirId(2);
            }
        });
        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedirId(3);
            }
        });

        limpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                habilitarBotonesYLimpiarCampos();
            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent otra = new Intent(Main2Activity.this, MainActivity.class);
                startActivity(otra);
            }
        });
        SQLiteDatabase tabla= base.getReadableDatabase();
        String SQL ="SELECT IDP, NOMBRE  FROM PROPIETARIO ";
        Cursor resultado =tabla.rawQuery(SQL, null);
        /*String [] idp= new String[100000];*/  int i=0;
       if(resultado.moveToFirst()){
           do{
               idp[i]=resultado.getString(0) + ": " + resultado.getString(1);
               resultado.moveToPosition(i);
               i++;
           }while (resultado.moveToNext());
       }
        identifiPro.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,idp));


    }

    private void eliminarDato(String idEliminar) {
        try{
            SQLiteDatabase tabla= base.getWritableDatabase();
            //String idEliminar = identificador.getText().toString();

            String SQL ="DELETE FROM INMUEBLE WHERE ID="+idEliminar;
            tabla.execSQL(SQL);
            Toast.makeText(this, "SE ELIMINO CORRECTAMENTE EL REGISTRO", Toast.LENGTH_LONG).show();
            habilitarBotonesYLimpiarCampos();
            tabla.close();
        }catch (SQLiteException e){
            Toast.makeText(this, "NO SE PUDO ELIMINAR EL REGISTRO", Toast.LENGTH_LONG).show();
        }
    }

    private void pedirId(final int origen ) {
        final EditText pidoID= new EditText(this);
        String mensaje="ESCRIBA EL ID A BUSCAR";
        String botonAccion="BUSCAR";

        pidoID.setInputType(InputType.TYPE_CLASS_NUMBER);
        pidoID.setHint("VALOR ENTERO MAYOR DE 0");


        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        if(origen ==2){
            mensaje="ESCRIBA ID A MODIFICAR";
            botonAccion="ACTUALIZAR";
        }
        if(origen ==3){
            mensaje="ESCRIBA ID QUE DESEA ELIMINAR";
            botonAccion="ELIMINAR";
        }
        alerta.setTitle("ATENCION").setMessage(mensaje)
                .setView(pidoID)
                .setPositiveButton(botonAccion, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(pidoID.getText().toString().isEmpty()){ //isEmpy si esta vacio
                            Toast.makeText(Main2Activity.this, "DEBES ESCRIBIR UN VALOR", Toast.LENGTH_LONG).show();
                            return;
                        }
                        buscarDato(pidoID.getText().toString(), origen);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("CANCELAR", null)
                .show();
    }

    private void buscarDato(String idBuscar, int origen) { //row query
        try{
            SQLiteDatabase tabla= base.getReadableDatabase();
            String SQL ="SELECT * FROM INMUEBLE WHERE ID="+idBuscar;

            Cursor resultado =tabla.rawQuery(SQL, null); //cursor permite navegar enre los renglones d ela consulta

            if(resultado.moveToFirst()){
                if(origen==3){
                    //significa que se consulto para borrar
                    String datos=idBuscar+"&"+resultado.getString(1)+"&"+resultado.getString(2)+"&"+resultado.getString(3)+"&"+resultado.getString(4)+"&"+resultado.getString(5);
                    confirmacionEliminarDatos(datos);
                    return;
                }
                //Si hay resultado y solo va a consultar
                identificador.setText(resultado.getString(0));
                domicilio.setText(resultado.getString(1));
                precioventa.setText(resultado.getString(2));
                preciorenta.setText(resultado.getString(3));
                fechatransaccion.setText(resultado.getString(4));
                String[] ii;
                int y=0;do{
                    ii=idp[y].split(": ");
                     y++;
                }while (!ii[0].equals(resultado.getString(5)));
                //Toast.makeText(this, ii[0]+"-"+String.valueOf(resultado.getString(5)), Toast.LENGTH_LONG).show();
                identifiPro.setSelection(y-1);
                if(origen==2){
                    //siginifica que se consulto para modificar
                    insertar.setEnabled(false);
                    consultar.setEnabled(false);
                    eliminar.setEnabled(false);
                    actualizar.setText("CONFIRMAR CAMBIOS");
                    identificador.setEnabled(false);
                }

            }else{
                //no hay resultados
                Toast.makeText(this, "NO SE ECONTRARON RESULTADOS!", Toast.LENGTH_LONG).show();
                habilitarBotonesYLimpiarCampos();
            }
            tabla.close();
        }catch (SQLiteException e){
            Toast.makeText(this, "NO SE PUDO REALIZAR LA BUSQUEDA", Toast.LENGTH_LONG).show();
        }

    }

    private void confirmacionEliminarDatos(String datos) {
        final String []cadena = datos.split("&");
        AlertDialog.Builder confir = new AlertDialog.Builder(this);

        confir.setTitle("IMPORTANTE").setMessage("¿Estas seguro que desea eliminar inmueble de domicilio?: "+cadena[1])
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eliminarDato(cadena[0]);
                        dialog.dismiss();
                    }
                }).setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                habilitarBotonesYLimpiarCampos();
                dialog.cancel();
            }
        }).show();
    }
    private void confirmacionActualizarDatos() {
        AlertDialog.Builder confir = new AlertDialog.Builder(this);

        confir.setTitle("IMPORTANTE").setMessage("¿Estas seguro que desea aplicar los cambios?")
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        actualizarDatos();
                        dialog.dismiss();
                    }
                }).setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                habilitarBotonesYLimpiarCampos();
                dialog.cancel();
            }
        }).show();
    }

    private void habilitarBotonesYLimpiarCampos() {
        identificador.setText("");
        domicilio.setText("");
        precioventa.setText("");
        preciorenta.setText("");
        fechatransaccion.setText("");
        insertar.setEnabled(true);
        consultar.setEnabled(true);
        eliminar.setEnabled(true);
        identifiPro.setSelected(false);
        identificador.setEnabled(true);
        actualizar.setText("ACTUALIZAR");
    }

    private void actualizarDatos() {
        try{
            String [] id=identifiPro.getSelectedItem().toString().split(": ");
            SQLiteDatabase tabla= base.getWritableDatabase();
            String SQL= "UPDATE INMUEBLE SET DOMICILIO='"+domicilio.getText().toString()+"',PRECIOVENTA="+precioventa.getText().toString()+
                    ", PRECIORENTA="+preciorenta.getText().toString()+
                    ", FECHATRANSACCION='"+fechatransaccion.getText().toString()+
                    "', IDP="+id[0]+" WHERE ID=" +identificador.getText().toString();
            tabla.execSQL(SQL);
            tabla.close();
            Toast.makeText(this, "SE ACTUALIZO", Toast.LENGTH_LONG).show();

        }catch (SQLiteException e){
            Toast.makeText(this, "NO SE PUDO ACTUALIZAR", Toast.LENGTH_LONG).show();
        }
        habilitarBotonesYLimpiarCampos();
    }

    private void codigoInsertar() {
        try{
            if(identificador.getText().toString().isEmpty()){
                Toast.makeText(this, "AGREGAR ID", Toast.LENGTH_LONG).show();
                return;
            }
            if(!repetidoId(identificador.getText().toString())) {
                String [] f= fechatransaccion.getText().toString().split("/");
                if(!(f[0].length()==4 && f[1].length()<=2 && f[2].length()<=2)) {
                    Toast.makeText(this, "ESCRIBIR FECHA CON EL FORMATO (YYYY/MM/DD)", Toast.LENGTH_LONG).show();
                    fechatransaccion.setText("");
                    return;
                }
                String [] id=identifiPro.getSelectedItem().toString().split(": ");
                SQLiteDatabase tabla = base.getWritableDatabase();
                String SQL = "INSERT INTO INMUEBLE VALUES(" + identificador.getText().toString()
                        + ",'" + domicilio.getText().toString() + "'," + precioventa.getText().toString()
                        + "," + preciorenta.getText().toString() + ",'" + fechatransaccion.getText().toString() + "'," + id[0] + ")";

                tabla.execSQL(SQL);
                tabla.close();
                Toast.makeText(this, "SE REALIZO LA INSERCION CORRECTAMENTE", Toast.LENGTH_LONG).show();
                //habilitarBotonesYLimpiarCampos();
            }else {
                Toast.makeText(this, "ID REPETIDO, INTRODUZCA OTRO", Toast.LENGTH_LONG).show();
                identificador.setText("");
            }
        }catch (SQLiteException e){
            Toast.makeText(this, "NO SE PUDO REALIZAR LA INSERCION", Toast.LENGTH_LONG).show();
            //habilitarBotonesYLimpiarCampos();
        }
    }
    private boolean repetidoId(String idBuscar){
        SQLiteDatabase tabla= base.getReadableDatabase();
        String SQL ="SELECT * FROM INMUEBLE WHERE ID="+idBuscar;

        Cursor resultado =tabla.rawQuery(SQL, null);

        if(resultado.moveToFirst()){
            return true;
        }
        return false;
    }

}
