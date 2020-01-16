package com.irs.jacksoncsvexample;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class App {
    
    private static final String FILE_NAME_CSV = "usuarios.csv";

    public static void main(String[] args) {
        System.out.println("BEGIN");
        try {
            List<Usuario> usuarios = new ArrayList<Usuario>();
            Usuario usuario = null;
            Date hoy = new Date();
            
            for (int i = 1; i <= 10; i++) {
                usuario = new Usuario();
                usuario.setIdUsuario(i);
                usuario.setNombre("nombre-" + i);
                usuario.setApellido1("apellido1-" + i);
                usuario.setApellido2("apellido2-" + i);
                usuario.setFechaNacimiento(hoy);
                usuarios.add(usuario);
            }
                    
            CsvSchema schema = CsvSchema.builder()
                    .setUseHeader(true)
                    .setColumnSeparator(';')
                    .disableQuoteChar()
                    .addColumn("idUsuario", CsvSchema.ColumnType.NUMBER)
                    .addColumn("nombre")
                    .addColumn("apellido1")
                    .addColumn("apellido2")
                    .addColumn("fechaNacimiento")
                    .build();
            
            CsvMapper mapper = new CsvMapper();
            mapper.setDateFormat(new SimpleDateFormat("dd/MM/yyyy"));
                        
            System.out.printf("Mostrando el contenido del archivo '%1$s' generado\n", FILE_NAME_CSV);
            String csv = mapper.writer(schema).writeValueAsString(usuarios);
	    System.out.println(csv);
                
            File csvFile = new File(FILE_NAME_CSV);
            
            System.out.printf("Generando archivo '%1$s'\n", csvFile.getName());
            ObjectWriter ow = mapper.writer(schema);
            ow.writeValue(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(csvFile), 1024), "UTF-8"), usuarios);
            
            System.out.printf("Procesando archivo '%1$s'\n", csvFile.getName());
            ObjectReader or = mapper.readerFor(Usuario.class).with(schema);
            MappingIterator<Usuario> it = or.readValues(csvFile);
            while (it.hasNextValue()) {
                Usuario u = it.next();
                System.out.printf("%1$d;%2$s;%3$s;%4$s;%5$td/%5$tm/%5$tY\n", u.getIdUsuario(), u.getNombre(), u.getApellido1(), u.getApellido2(), u.getFechaNacimiento());
            }
            // Tambien puedo leer todas las lineas a la vez, en lugar de una en una
            List<Usuario> listUsuarios = it.readAll();
            
            System.out.println("Mostrando la lista de usuarios en formato CSV");
	    System.out.println(toCsv(usuarios));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("END");
    }
    
    private static String toCsv(List<Usuario> usuarios) throws JsonProcessingException {
        CsvMapper mapper = new CsvMapper();
        mapper.setDateFormat(new SimpleDateFormat("dd/MM/yyyy"));
         
        CsvSchema schema = mapper.schemaFor(Usuario.class).withHeader();
        
        return mapper.writer(schema).writeValueAsString(usuarios);
    }
}
