package mx.edu.ittepic.dadm_u4_practica4_arleymagnoliaaquinogarcia;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText identificador, nombre, domicilio, telefono;
    Button consultar, insertar, eliminar, actualizar, limpiar;
    ImageButton regresar;

    BaseDatos base; //basedatos es mi clase que construi y este heradeda de SQLHelper, conecta bd con dbms
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        identificador=findViewById(R.id.idd);
        nombre=findViewById(R.id.nombre);
        domicilio=findViewById(R.id.domicilio);
        telefono=findViewById(R.id.tel);

        insertar=findViewById(R.id.insertar);
        consultar=findViewById(R.id.consultar);
        eliminar=findViewById(R.id.borrar);
        actualizar=findViewById(R.id.actualizar);
        limpiar=findViewById(R.id.limpiar);
        regresar=findViewById(R.id.b2);

        //asignar memoria y configuracion
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
        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent otra = new Intent(MainActivity.this, Main3Activity.class);
                startActivity(otra);
            }
        });


    }

    private void eliminarDato(String idEliminar) {
        try{
            SQLiteDatabase tabla= base.getWritableDatabase();
            //String idEliminar = identificador.getText().toString();

            String SQL ="DELETE FROM PROPIETARIO WHERE IDP="+idEliminar;//identificador.getText().toString();
            String SQLi ="DELETE FROM INMUEBLE WHERE IDP="+idEliminar;
            tabla.execSQL(SQLi);
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
                            Toast.makeText(MainActivity.this, "DEBES ESCRIBIR UN VALOR", Toast.LENGTH_LONG).show();
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
            String SQL ="SELECT * FROM PROPIETARIO WHERE IDP="+idBuscar;

            Cursor resultado =tabla.rawQuery(SQL, null); //cursor permite navegar enre los renglones d ela consulta

            if(resultado.moveToFirst()){
                if(origen==3){
                    //significa que se consulto para borrar
                    String datos=idBuscar+"&"+resultado.getString(1)+"&"+resultado.getString(2)+"&"+resultado.getString(3);
                    confirmacionEliminarDatos(datos);
                    return;
                }
                //Si hay resultado
                identificador.setText(resultado.getString(0));
                nombre.setText(resultado.getString(1));
                domicilio.setText(resultado.getString(2));
                telefono.setText(resultado.getString(3));
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
            }
            tabla.close();
        }catch (SQLiteException e){
            Toast.makeText(this, "NO SE PUDO REALIZAR LA BUSQUEDA", Toast.LENGTH_LONG).show();
        }

    }

    private void confirmacionEliminarDatos(String datos) {
        final String []cadena = datos.split("&");
        AlertDialog.Builder confir = new AlertDialog.Builder(this);

        confir.setTitle("IMPORTANTE").setMessage("¿Estas seguro que desea eliminar a?: "+cadena[1])
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
        nombre.setText("");
        domicilio.setText("");
        telefono.setText("");
        insertar.setEnabled(true);
        consultar.setEnabled(true);
        eliminar.setEnabled(true);
        identificador.setEnabled(true);
        actualizar.setText("ACTUALIZAR");
    }

    private void actualizarDatos() {
        try{
            SQLiteDatabase tabla= base.getWritableDatabase();
            String SQL= "UPDATE PROPIETARIO SET NOMBRE='"+nombre.getText().toString()+"',DOMICILIO='"+domicilio.getText().toString()+"', TELEFONO='"+telefono.getText().toString()+"'WHERE IDP="
                    +identificador.getText().toString();

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
                SQLiteDatabase tabla = base.getWritableDatabase();
                String SQL = "INSERT INTO PROPIETARIO VALUES(" + identificador.getText().toString()
                        + ",'" + nombre.getText().toString() + "','" + domicilio.getText().toString()
                        + "','" + telefono.getText().toString() + "')";

                tabla.execSQL(SQL);//ejecuta la instruccion SQL IMPORTANTE PONER
                tabla.close(); //cierro la conexion a la bd
                Toast.makeText(this, "SE REALIZO LA INSERCION CORRECTAMENTE", Toast.LENGTH_LONG).show();
                habilitarBotonesYLimpiarCampos();
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
        String SQL ="SELECT * FROM PROPIETARIO WHERE IDP="+idBuscar;

        Cursor resultado =tabla.rawQuery(SQL, null); //cursor permite navegar enre los renglones d ela consulta

        if(resultado.moveToFirst()){
            return true;
        }
        return false;
    }
}
