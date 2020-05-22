/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GestioneSpesaWS;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.*;
import javax.ws.rs.*;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * REST Web Service
 *
 * @author Luca
 */
@ApplicationPath("/")
@Path("gestioneSpesa")
public class GestioneSpesa extends Application {

    final private String driver = "com.mysql.jdbc.Driver";
    final private String dbms_url = "jdbc:mysql://localhost/";
    final private String database = "test_database";
    final private String user = "root";
    final private String password = "";
    private Connection spesaDatabase;
    private boolean connected;

    public GestioneSpesa() {
        super();
    }

    public void init() {
        String url = dbms_url + database;
        try {
            Class.forName(driver);
            spesaDatabase = DriverManager.getConnection(url, user, password);
            connected = true;
        } catch (SQLException e) {
            connected = false;
        } catch (ClassNotFoundException e) {
            connected = false;
        }
    }

    public void destroy() {
        try {
            spesaDatabase.close();
        } catch (SQLException e) {
        }
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("getRisposte")
    public String getRisposte(@QueryParam("id") int id) {
        init();
        String output = "";
        if (!connected) {
            return "<errorMessage>400</errorMessage>";
        }
        
        try {
            String sql = "SELECT idRisposta,rifRichiesta FROM risposte WHERE";
            if (id != 0) {
                sql = sql + " rifUtente='" + id + "' AND";
            }

            sql = sql + " 1";
            Statement statement = spesaDatabase.createStatement();
            ResultSet result = statement.executeQuery(sql);
            
            ArrayList<Risposta> risp = new ArrayList<Risposta>();
            while (result.next()) {
                int rispID = result.getInt(1);
                int rispRifRichiesta = result.getInt(2);

                risp.add(new Risposta(rispID, id, rispRifRichiesta));

            }
            
            if (risp.size() > 0) {
                result.close();
                statement.close();

                output = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
                output = output + "<return>\n";

                for (int i = 0; i < risp.size(); i++) {
                    output = output + "<risposta>\n";
                    output = output + "<idRisposta>" + risp.get(i).getIdRisposta()+ "</idRisposta>\n";
                    output = output + "<rifUtente>" + risp.get(i).getRifUtente() + "</rifUtente>\n";
                    output = output + "<rifRichiesta>" + risp.get(i).getRifRichiesta()+ "</rifRichiesta>\n";
                    output = output + "</risposta>\n";
                }

                output = output + "</return>\n";
            } else {
                result.close();
                statement.close();
                destroy();
                return "<errorMessage>404</errorMessage>";
            }
        } catch (SQLException ex) {
            destroy();
            return "<errorMessage>500</errorMessage>";
        }
        destroy();
        return output;
    }

    @GET
    @Produces(MediaType.TEXT_XML)
    @Path("getProdotto")
    public String getProdotto(@QueryParam("genere") String genere, @QueryParam("etichetta") String etichetta, @QueryParam("costo") double costo, @QueryParam("nome") String nome, @QueryParam("marca") String marca, @QueryParam("descrizione") String descrizione) {
        init();
        String output = "";
        if (!connected) {
            return "<errorMessage>400</errorMessage>";
        }

        try {
            String sql = "SELECT * FROM prodotti WHERE";
            if (genere != null) {
                sql = sql + " genere='" + genere + "' AND";
            }
            if (etichetta != null) {
                sql = sql + " etichetta='" + etichetta + "' AND";
            }
            if (costo != 0.00) {
                sql = sql + " costo='" + costo + "' AND";
            }
            if (nome != null) {
                sql = sql + " nome='" + nome + "' AND";
            }
            if (marca != null) {
                sql = sql + " marca='" + marca + "' AND";
            }

            sql = sql + " 1";
            Statement statement = spesaDatabase.createStatement();
            ResultSet result = statement.executeQuery(sql);

            ArrayList<Prodotto> prd = new ArrayList<Prodotto>();
            while (result.next()) {
                int prdID = result.getInt(1);
                String prdGenere = result.getString(2);
                String prdEtichetta = result.getString(3);
                double prdCosto = result.getDouble(4);
                String prdNome = result.getString(5);
                String prdMarca = result.getString(6);
                String prdDescrizione = result.getString(7);

                prd.add(new Prodotto(prdID, prdGenere, prdEtichetta, prdCosto, prdNome, prdMarca, prdDescrizione));

            }

            if (prd.size() > 0) {
                result.close();
                statement.close();

                output = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
                output = output + "<return>\n";

                for (int i = 0; i < prd.size(); i++) {
                    output = output + "<prodotto>\n";
                    output = output + "<idProdotto>" + prd.get(i).getIdProdotto() + "</idProdotto>\n";
                    output = output + "<genere>" + prd.get(i).getGenere() + "</genere>\n";
                    output = output + "<etichetta>" + prd.get(i).getEtichetta() + "</etichetta>\n";
                    output = output + "<costo>" + prd.get(i).getCosto() + "</costo>\n";
                    output = output + "<nome>" + prd.get(i).getNome() + "</nome>\n";
                    output = output + "<marca>" + prd.get(i).getMarca() + "</marca>\n";
                    output = output + "<descrizione>" + prd.get(i).getDescrizione() + "</descrizione>\n";
                    output = output + "</prodotto>\n";
                }

                output = output + "</return>\n";
            } else {
                result.close();
                statement.close();
                destroy();
                return "<errorMessage>404</errorMessage>";
            }
        } catch (SQLException ex) {
            destroy();
            return "<errorMessage>500</errorMessage>";
        }
        destroy();
        return output;
    }

    @POST
    @Consumes(MediaType.TEXT_XML)
    @Path("postProdotto")
    public String postProdotto(String content) {
        try {
            init();

            MyParser myParse = new MyParser();
            BufferedWriter writer;
            writer = new BufferedWriter(new FileWriter("entry.xml"));
            writer.write(content);
            writer.flush();
            writer.close();

            ArrayList<Prodotto> prodotti = (ArrayList<Prodotto>) myParse.parseDocument("entry.xml");
            if (!connected) {
                return "<errorMessage>400</errorMessage>";
            }

            try {
                String sql = "INSERT INTO prodotti (genere,etichetta,costo,nome,marca,descrizione) VALUES ('" + prodotti.get(0).getGenere() + "','" + prodotti.get(0).getEtichetta() + "','" + prodotti.get(0).getCosto() + "','" + prodotti.get(0).getNome() + "','" + prodotti.get(0).getMarca() + "','" + prodotti.get(0).getDescrizione() + "')";
                Statement statement = spesaDatabase.createStatement();

                if (statement.executeUpdate(sql) <= 0) {
                    statement.close();
                    return "<errorMessage>403</errorMessage>";
                }

                statement.close();
                destroy();
                return "<message>Inserimento avvenuto correttamente</message>";
            } catch (SQLException ex) {
                destroy();
                return "<errorMessage>500</errorMessage>";
            }
        } catch (IOException ex) {
            Logger.getLogger(GestioneSpesa.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(GestioneSpesa.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(GestioneSpesa.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "<errorMessage>400</errorMessage>";
    }

    @PUT
    @Consumes(MediaType.TEXT_XML)
    @Path("putProdotto")
    public String putProdotto(String content) {
        try {
            init();

            MyParser myParse = new MyParser();
            BufferedWriter writer;
            writer = new BufferedWriter(new FileWriter("entry.xml"));
            writer.write(content);
            writer.flush();
            writer.close();

            ArrayList<Prodotto> prodotto = (ArrayList<Prodotto>) myParse.parseDocument("entry.xml");
            if (!connected) {
                return "<errorMessage>400</errorMessage>";
            }

            try {
                String sql = "UPDATE prodotti SET nome='" + prodotto.get(0).getNome() + "', genere='" + prodotto.get(0).getGenere() + "', etichetta='" + prodotto.get(0).getEtichetta() + "', costo='" + prodotto.get(0).getCosto() + "', nome='" + prodotto.get(0).getNome() + "', marca='" + prodotto.get(0).getMarca() + "', descrizione='" + prodotto.get(0).getDescrizione() + "' WHERE idProdotto='" + prodotto.get(0).getIdProdotto() + "'";
                Statement statement = spesaDatabase.createStatement();

                if (statement.executeUpdate(sql) <= 0) {
                    statement.close();
                    return "<errorMessage>403</errorMessage>";
                }

                statement.close();
                destroy();
                return "<message>Update avvenuto correttamente</message>";
            } catch (SQLException ex) {
                destroy();
                return "<errorMessage>500</errorMessage>";
            }
        } catch (IOException ex) {
            Logger.getLogger(GestioneSpesa.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(GestioneSpesa.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(GestioneSpesa.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "<errorMessage>400</errorMessage>";
    }

    @DELETE
    @Path("deleteProdotto")
    public String deleteProdotto(@QueryParam("id") int id) {
        init();

        if (!connected) {
            return "<errorMessage>400</errorMessage>";
        }

        if (id != 0) {
            try {
                String sql = "DELETE FROM prodotti WHERE idProdotto='" + id + "'";
                Statement statement = spesaDatabase.createStatement();

                if (statement.executeUpdate(sql) <= 0) {
                    statement.close();
                    return "<errorMessage>403</errorMessage>";
                }

                statement.close();
                destroy();
                return "<message>Eliminazione avvenuta correttamente</message>";
            } catch (SQLException ex) {
                destroy();
                return "<errorMessage>500</errorMessage>";
            }
        } else {
            return "<errorMessage>406</errorMessage>";
        }

    }
}
