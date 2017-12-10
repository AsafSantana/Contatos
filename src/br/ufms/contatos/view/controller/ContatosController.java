/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufms.contatos.view.controller;

import br.ufms.contatos.model.dao.ContatoDAO;
import br.ufms.contatos.model.domain.Contato;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.jfoenix.controls.JFXButton;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author Asaf Santana
 */
public class ContatosController implements Initializable {

    private final ContatoDAO contatoDAO = new ContatoDAO();

    private Contato selecionado;

    private boolean flagAdicionar = false, flagEditar = false;

    private List<Contato> listaDeContatos = new ArrayList<>();

    private ObservableList<Contato> observarlista;

    @FXML
    private ListView<Contato> lista;

    @FXML
    private JFXButton adicionar;

    @FXML
    private JFXButton editar;

    @FXML
    private JFXButton excluir;
    @FXML
    private JFXButton salvar;

    @FXML
    private JFXButton cancelar;

    @FXML
    private JFXButton fechar;

    @FXML
    private JFXButton relatorio;

    @FXML
    private TextField pesquisar;

    @FXML
    private TextField nome;

    @FXML
    private TextField telefone;

    @FXML
    private TextField email;

    /**
     *  /**
     * Quando clica para editar
     *
     * @param event
     */
    @FXML
    private void clicouEditar(ActionEvent event) {
        if (selecionado != null) {
            //editar.visibleProperty().bindBidirectional(other);
            editar.setDisable(true);
            relatorio.setDisable(true);
            lista.setDisable(true);
            camposAbertos();
            salvar.setDisable(false);
            cancelar.setDisable(false);
            adicionar.setDisable(true);
            excluir.setDisable(true);
            flagEditar = true;
        } else {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setHeaderText("Item não selecionado.");
            alerta.show();
        }
    }

    /**
     * Quando clica para adicionar
     *
     * @param event
     */
    @FXML
    private void clicouAdicionar(ActionEvent event) {
        limparCampos();
        camposAbertos();
        adicionar.setDisable(true);
        relatorio.setDisable(true);
        lista.setDisable(true);
        salvar.setDisable(false);
        cancelar.setDisable(false);
        excluir.setDisable(true);
        editar.setDisable(true);
        flagAdicionar = true;
    }

    /**
     * Quando clica para excluir
     *
     * @param event
     */
    @FXML
    private void clicouExcluir(ActionEvent event) {
        if (selecionado != null) {

            try {
                Contato contato = lista.getSelectionModel().getSelectedItem();
                observarlista.remove(contato);
                listaDeContatos.remove(contato);
                contatoDAO.delete(contato.getId());
                limparCampos();
            } catch (SQLException ex) {
                Alert alerta = new Alert(Alert.AlertType.ERROR);
                alerta.setHeaderText("Erro ao excluir.");
                alerta.show();
            }
        } else {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setHeaderText("Item não selecionado.");
            alerta.show();
        }
    }

    /**
     * Quando clica para cancelar
     *
     * @param event
     */
    @FXML
    private void clicouCancelar(ActionEvent event) {
        flagAdicionar = false;
        flagEditar = false;
        lista.setDisable(false);
        adicionar.setDisable(false);
        editar.setDisable(false);
        excluir.setDisable(false);

        camposFechados();
        relatorio.setDisable(false);
    }

    /**
     * Quando clica para salvar
     *
     * @param event
     */
    @FXML
    private void clicouSalvar(ActionEvent event) {
        lista.setDisable(false);
        adicionar.setDisable(false);
        editar.setDisable(false);
        excluir.setDisable(false);
        camposFechados();

        if (nome.getText().equals("") && email.getText().equals("") && telefone.getText().equals("")) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setHeaderText("Nenhum campo preenchido.");
            alerta.show();
        } else {
            if (flagAdicionar) {

                try {

                    Contato con = new Contato();
                    con.setNome(nome.getText());
                    con.setEmail(email.getText());
                    con.setTelefone(telefone.getText());
                    contatoDAO.insert(con);
                    listaDeContatos.add(con);
                    observarlista.add(con);
                    carregarListViewContatos();
                    relatorio.setDisable(false);
                    flagAdicionar = false;

                } catch (SQLException ex) {
                    Alert alerta = new Alert(Alert.AlertType.ERROR);
                    alerta.setHeaderText("Erro ao adicionar.");
                    alerta.show();
                    limparCampos();
                }

            } else if (flagEditar) {
                selecionado.setNome(nome.getText());
                selecionado.setEmail(email.getText());
                selecionado.setTelefone(telefone.getText());
                observarlista.set(0, selecionado);
                carregarListViewContatos();
                relatorio.setDisable(false);
                try {
                    contatoDAO.update(selecionado);

                } catch (SQLException ex) {
                    Alert alerta = new Alert(Alert.AlertType.ERROR);
                    alerta.setHeaderText("Erro ao editar.");
                    alerta.show();
                }

                flagEditar = false;
            }

        }
    }

    @FXML

    /**
     * carrega os contatos na ListView
     */
    public void carregarListViewContatos() {
        observarlista = FXCollections.observableArrayList(listaDeContatos);
        lista.setItems(observarlista);

    }

    /**
     * faz uma busca na listview
     */
    private void busca() {
        ObservableList<Contato> contatopesquisa = FXCollections.observableArrayList();
        // pode implementar algum tipo de filtro 
        for (int i = 0; i < listaDeContatos.size(); i++) {
            if (listaDeContatos.get(i).getNome().toLowerCase().contains(pesquisar.getText().toLowerCase())) {
                contatopesquisa.add(listaDeContatos.get(i));
            }
        }
        lista.setItems(contatopesquisa);

    }

    @FXML
    /**
     * a ListView responde com o mouse
     */
    void clicarMouseListView() {
        Contato contato = lista.getSelectionModel().getSelectedItem();
        nome.setText(contato.getNome());
        telefone.setText(contato.getTelefone());
        email.setText(contato.getEmail());

    }

    /**
     * limpa os campos de escrita, desabilita botoes e carrega a listview
     */
    public void limparCampos() {
        pesquisar.clear();
        nome.clear();
        email.clear();
        telefone.clear();
        salvar.setDisable(true);
        cancelar.setDisable(true);
        carregarListViewContatos();
    }

    /**
     * abre os campos de escrita
     */
    public void camposAbertos() {
        nome.setEditable(true);
        telefone.setEditable(true);
        email.setEditable(true);
    }

    /**
     * fecha os campos de escrita
     */
    public void camposFechados() {
        nome.setEditable(false);
        telefone.setEditable(false);
        email.setEditable(false);
    }

    /**
     * metodo para gerar o pdf.
     */
    public void gerarPdf() {
        Document doc = new Document();
        try {
            try {
                PdfWriter.getInstance(doc, new FileOutputStream(System.getProperty("user.home") + File.separator + "contatos.pdf"));
                doc.open();
                List<Contato> contatos = listaDeContatos;
                for (int i = 0; i < contatos.size(); i++) {
                    doc.add(new Paragraph("\n\nContato: " + i + "\n\n"));
                    doc.add(new Paragraph("Nome: " + contatos.get(i).getNome()));
                    doc.add(new Paragraph("Telefone: " + contatos.get(i).getTelefone()));
                    doc.add(new Paragraph("Email: " + contatos.get(i).getEmail()));

                }
                Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
                alerta.setHeaderText("Esta na sua pasta de Usuário");
                alerta.show();

                doc.close();
            } catch (DocumentException ex) {
                Alert alerta = new Alert(Alert.AlertType.ERROR);
                alerta.setHeaderText("Erro ao adicionar PDF.");
                alerta.show();
                Logger.getLogger(ContatosController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setHeaderText("Erro ao adicionar PDF.");
            alerta.show();
            Logger.getLogger(ContatosController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        pesquisar.setOnKeyReleased((KeyEvent e) -> {
            busca();
        });

        relatorio.setOnMouseClicked((MouseEvent e) -> {
            gerarPdf();
        });

        fechar.setOnMouseClicked((MouseEvent e) -> {
            System.exit(0);
        });

        lista.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override

            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                selecionado = (Contato) newValue;
            }
        });

        atualizar();

        camposFechados();
        salvar.setDisable(true);
        cancelar.setDisable(true);
        carregarListViewContatos();

    }

    /**
     *
     * atualiza os dados colocandona listview
     */
    private void atualizar() {
        try {
            listaDeContatos = contatoDAO.getAll();

        } catch (SQLException ex) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setHeaderText("Erro no Banco de Dados");
            alerta.show();

        }
    }
}
