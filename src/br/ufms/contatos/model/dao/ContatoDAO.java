/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufms.contatos.model.dao;

import br.ufms.contatos.model.domain.Contato;
import br.ufms.contatos.util.DatabaseManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe que faz persistência
 *
 * @author Asaf Santana
 */
public class ContatoDAO {

    private final DatabaseManager db;

    public ContatoDAO() {
        db = DatabaseManager.getInstance();
    }

    public void insert(Contato contato) throws SQLException {
        String sql = "INSERT INTO contatos.contato (nome, telefone, email) VALUES (?, ?, ?)";
        try (Connection conn = db.getConnection(); PreparedStatement ps
                = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, contato.getNome());
            ps.setString(2, contato.getTelefone());
            ps.setString(3, contato.getEmail());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.first()) {
                    contato.setId(rs.getInt(1));
                }

            }
        }
    }

    public void update(Contato contato) throws SQLException {
        String sql = "UPDATE contatos.contato SET nome = ?, telefone = ?, email = ? WHERE id = ?";
        try (Connection conn = db.getConnection(); PreparedStatement ps
                = conn.prepareStatement(sql)) {

            ps.setString(1, contato.getNome());
            ps.setString(2, contato.getTelefone());
            ps.setString(3, contato.getEmail());
            ps.setInt(4, contato.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM contatos.contato WHERE id = ?";
        try (Connection conn = db.getConnection(); PreparedStatement ps
                = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }

    }

    public Contato get(int id) throws SQLException {
        String sql = "SELECT * FROM contatos.contato WHERE id = ?";
        Contato c = null;

        try (Connection conn = db.getConnection(); PreparedStatement ps
                = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.first()) {
                    c = new Contato();
                    c.setId(rs.getInt("id"));
                    c.setNome(rs.getString("nome"));
                    c.setTelefone(rs.getString("telefone"));
                    c.setEmail(rs.getString("email"));

                }
            }
        }

        return c;
    }

    public List<Contato> getAll() throws SQLException {
        String sql = "SELECT * FROM contatos.contato";
        List<Contato> lista = new ArrayList<>();

        try (Connection conn = db.getConnection(); PreparedStatement ps
                = conn.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Contato c = new Contato();
                    c.setId(rs.getInt("id"));
                    c.setNome(rs.getString("nome"));
                    c.setTelefone(rs.getString("telefone"));
                    c.setEmail(rs.getString("email"));
                    lista.add(c);
                }
            }
        }
        return lista;
    }
}
