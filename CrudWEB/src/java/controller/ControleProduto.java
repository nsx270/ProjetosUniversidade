/**
 *
 * @author pedro
 */
package controller;

import DAO.ProdutoDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.List;
import model.Produto;

@WebServlet(name = "ControleProduto", urlPatterns = {"/ControleProduto"})
public class ControleProduto extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            
            String op = request.getParameter("op");

            ProdutoDAO pdao = new ProdutoDAO();
            Produto p = new Produto();
            String mensagem = "";
            String paginaDestino = "";

            if (op == null || op.isEmpty()) {
                mensagem = "Operação não especificada ou inválida.";
                paginaDestino = "principal.html";
                request.setAttribute("msg", mensagem);
            } else if (op.equals("CADASTRAR")) {
                String nome = request.getParameter("txtnome");
                double preco = 0;
                int quantidadeInt = 0;
                String quantidadeStr = request.getParameter("txtquantidade");

                try {
                    preco = Double.parseDouble(request.getParameter("txtpreco"));
                } catch (NumberFormatException e) {
                    mensagem = "Preço inválido.";
                    request.setAttribute("msg", mensagem);
                    paginaDestino = "cadastrar.jsp";
                }

                if (mensagem.isEmpty()) {
                    try {
                        quantidadeInt = Integer.parseInt(quantidadeStr);
                        if (quantidadeInt < 0) {
                            mensagem = "Quantidade inválida";
                            request.setAttribute("msg", mensagem);
                            paginaDestino = "cadastrar.jsp";
                        }
                    } catch (NumberFormatException e) {
                        mensagem = "Quantidade inválida.";
                        request.setAttribute("msg", mensagem);
                        paginaDestino = "cadastrar.jsp";
                    }
                }
                
                if (mensagem.isEmpty()) {
                    String descricao = request.getParameter("txtdescricao");
                    p.setNome(nome);
                    p.setPreco(preco);
                    p.setDescricao(descricao);
                    p.setQuantidade(quantidadeInt);
                    try {
                        pdao.cadastrar(p);
                        mensagem = "Cadastrado com sucesso.";
                    } catch (ClassNotFoundException | SQLException ex) {
                        mensagem = "ERRO ao cadastrar: " + ex.getMessage();
                    }
                    request.setAttribute("msg", mensagem);
                    paginaDestino = "info/resultadocadastrar.jsp";
                }
            } else if (op.equals("listarParaDeletar")) {
                 try {
                    List<Produto> listaProdutos = pdao.consultarTodos();
                    request.setAttribute("listaProdutos", listaProdutos);
                } catch (ClassNotFoundException | SQLException ex) {
                    mensagem = "ERRO ao listar produtos para deletar: " + ex.getMessage();
                    request.setAttribute("msg", mensagem);
                }
                paginaDestino = "deletar.jsp";

            } else if (op.equals("DELETAR")) {
                String idProdutoStrDel = request.getParameter("produtoId");
                if (idProdutoStrDel != null && !idProdutoStrDel.isEmpty()) {
                    try {
                        int idProduto = Integer.parseInt(idProdutoStrDel);
                        p.setId(idProduto);
                        pdao.deletar(p);
                        mensagem = "Produto deletado com sucesso!";
                    } catch (NumberFormatException ex) {
                        mensagem = "ERRO: ID do produto inválido para deleção. " + ex.getMessage();
                    } catch (ClassNotFoundException | SQLException ex) {
                        mensagem = "ERRO ao deletar produto: " + ex.getMessage();
                    }
                } else {
                    mensagem = "ERRO: Nenhum produto selecionado para deletar.";
                }
                try {
                    List<Produto> listaProdutos = pdao.consultarTodos();
                    request.setAttribute("listaProdutos", listaProdutos);
                } catch (ClassNotFoundException | SQLException ex) {
                    mensagem += (mensagem.isEmpty() ? "" : " ") + "ERRO ao recarregar lista pós-deleção: " + ex.getMessage();
                }
                request.setAttribute("msg", mensagem);
                paginaDestino = "deletar.jsp";

            } else if (op.equals("listarParaAtualizar")) {
                try {
                    List<Produto> listaProdutos = pdao.consultarTodos();
                    request.setAttribute("listaProdutos", listaProdutos);
                } catch (ClassNotFoundException | SQLException ex) {
                    mensagem = "ERRO ao listar produtos para atualização: " + ex.getMessage();
                    request.setAttribute("msg", mensagem);
                }
                paginaDestino = "atualizar.jsp";

            } else if (op.equals("carregarParaEditar")) {
                String idProdutoStrEdit = request.getParameter("produtoId");
                if (idProdutoStrEdit != null && !idProdutoStrEdit.isEmpty()) {
                    try {
                        int idProduto = Integer.parseInt(idProdutoStrEdit);
                        Produto produtoParaEditar = new Produto(); 
                        produtoParaEditar.setId(idProduto);
                        produtoParaEditar = pdao.consultarById(produtoParaEditar);
                        
                        if (produtoParaEditar != null && produtoParaEditar.getNome() != null) {
                             request.setAttribute("produtoParaEditar", produtoParaEditar);
                             paginaDestino = "editarProduto.jsp"; 
                        } else {
                            mensagem = "ERRO: Produto com ID " + idProduto + " não encontrado.";
                            paginaDestino = "atualizar.jsp";
                            try {
                                List<Produto> listaProdutos = pdao.consultarTodos();
                                request.setAttribute("listaProdutos", listaProdutos);
                            } catch (ClassNotFoundException | SQLException exSQL) {
                                mensagem += (mensagem.isEmpty() ? "" : " ") + "ERRO adicional ao recarregar lista: " + exSQL.getMessage();
                            }
                        }
                    } catch (NumberFormatException ex) {
                        mensagem = "ERRO: ID do produto inválido para edição. " + ex.getMessage();
                        paginaDestino = "atualizar.jsp"; 
                         try {
                            List<Produto> listaProdutos = pdao.consultarTodos();
                            request.setAttribute("listaProdutos", listaProdutos);
                        } catch (ClassNotFoundException | SQLException exSQL) {
                             mensagem += (mensagem.isEmpty() ? "" : " ") + "ERRO adicional ao recarregar lista: " + exSQL.getMessage();
                        }
                    } catch (ClassNotFoundException | SQLException ex) {
                        mensagem = "ERRO ao carregar produto para edição: " + ex.getMessage();
                        paginaDestino = "atualizar.jsp"; 
                         try {
                            List<Produto> listaProdutos = pdao.consultarTodos();
                            request.setAttribute("listaProdutos", listaProdutos);
                        } catch (ClassNotFoundException | SQLException exSQL) {
                             mensagem += (mensagem.isEmpty() ? "" : " ") + "ERRO adicional ao recarregar lista: " + exSQL.getMessage();
                        }
                    }
                } else {
                    mensagem = "ERRO: Nenhum produto selecionado para edição.";
                    paginaDestino = "atualizar.jsp"; 
                     try {
                        List<Produto> listaProdutos = pdao.consultarTodos();
                        request.setAttribute("listaProdutos", listaProdutos);
                    } catch (ClassNotFoundException | SQLException exSQL) {
                         mensagem += (mensagem.isEmpty() ? "" : " ") + "ERRO adicional ao recarregar lista: " + exSQL.getMessage();
                    }
                }
                request.setAttribute("msg", mensagem);
            
            } else if (op.equals("SALVAR_ATUALIZACAO")) {
                String idProdutoAtualizarStr = request.getParameter("produtoId");
                String nome = request.getParameter("txtnome");
                String precoStr = request.getParameter("txtpreco");
                String descricao = request.getParameter("txtdescricao");
                String quantidadeStr = request.getParameter("txtquantidade");

                if (idProdutoAtualizarStr != null && !idProdutoAtualizarStr.isEmpty() && 
                    precoStr != null && !precoStr.isEmpty() &&
                    quantidadeStr != null && !quantidadeStr.isEmpty()) {
                    try {
                        Produto produtoAtualizado = new Produto();
                        produtoAtualizado.setId(Integer.parseInt(idProdutoAtualizarStr));
                        produtoAtualizado.setNome(nome);
                        produtoAtualizado.setPreco(Double.parseDouble(precoStr));
                        produtoAtualizado.setDescricao(descricao);
                        int quantidadeInt = Integer.parseInt(quantidadeStr);
                        if (quantidadeInt < 0) {
                             mensagem = "ERRO: Quantidade não pode ser negativa.";
                             // Como 'paginaDestino' é 'atualizar.jsp' de qualquer forma e a lista é recarregada,
                             // a mensagem de erro será exibida. Não é necessário alterar paginaDestino aqui.
                        } else {
                            produtoAtualizado.setQuantidade(quantidadeInt);
                            pdao.atualizar(produtoAtualizado);
                            mensagem = "Produto atualizado com sucesso!";
                        }

                    } catch (NumberFormatException ex) {
                        mensagem = "ERRO: Dados numéricos inválidos para atualização (ID, Preço ou Quantidade).";
                    } catch (ClassNotFoundException | SQLException ex) {
                        mensagem = "ERRO ao atualizar produto: " + ex.getMessage();
                    }
                } else {
                    mensagem = "ERRO: Dados incompletos para atualização.";
                }
                try {
                    List<Produto> listaProdutos = pdao.consultarTodos();
                    request.setAttribute("listaProdutos", listaProdutos);
                } catch (ClassNotFoundException | SQLException ex) {
                    mensagem += (mensagem.isEmpty() ? "" : " ") + "ERRO ao recarregar lista pós-atualização: " + ex.getMessage();
                }
                request.setAttribute("msg", mensagem);
                paginaDestino = "atualizar.jsp";
                
            } else if (op.equals("consultarPorId")) {
                String idStr = request.getParameter("produtoId");
                if (idStr != null && !idStr.isEmpty()) {
                    try {
                        int idProduto = Integer.parseInt(idStr);
                        Produto produtoConsultado = new Produto();
                        produtoConsultado.setId(idProduto);
                        produtoConsultado = pdao.consultarById(produtoConsultado);

                        if (produtoConsultado != null && produtoConsultado.getNome() != null) {
                            request.setAttribute("produtoConsultado", produtoConsultado);
                        } else {
                            mensagem = "Produto com ID " + idProduto + " não encontrado.";
                        }
                    } catch (NumberFormatException ex) {
                        mensagem = "ERRO: ID do produto deve ser um número. " + ex.getMessage();
                    } catch (ClassNotFoundException | SQLException ex) {
                        mensagem = "ERRO ao consultar produto por ID: " + ex.getMessage();
                    }
                } else {
                    mensagem = "ERRO: Por favor, forneça um ID para consulta.";
                }
                request.setAttribute("msg", mensagem);
                paginaDestino = "consultarProduto.jsp";

            } else if (op.equals("consultarTodos")) {
                try {
                    List<Produto> listaProdutos = pdao.consultarTodos();
                    if (listaProdutos != null && !listaProdutos.isEmpty()) {
                        request.setAttribute("listaProdutosConsultados", listaProdutos);
                    } else {
                        mensagem = "Não há produtos cadastrados para listar.";
                    }
                } catch (ClassNotFoundException | SQLException ex) {
                    mensagem = "ERRO ao listar todos os produtos: " + ex.getMessage();
                }
                request.setAttribute("msg", mensagem);
                paginaDestino = "consultarProduto.jsp";
            } else {
                 mensagem = "Operação '" + op + "' não reconhecida.";
                 paginaDestino = "principal.html";
                 request.setAttribute("msg", mensagem);
            }
            
            String finalMessage = (String) request.getAttribute("msg");

            if (paginaDestino != null && !paginaDestino.isEmpty()) {
                try {
                    request.getRequestDispatcher(paginaDestino).forward(request, response);
                } catch (ServletException | IOException e) {
                    response.setContentType("text/html;charset=UTF-8");
                    if (!response.isCommitted()) {
                        out.println("<!DOCTYPE html><html><head><title>Erro no Forward</title></head><body>");
                        out.println("<h1>Erro Crítico no Forward</h1>");
                        out.println("<p>Não foi possível encaminhar para a página: <strong>" + paginaDestino + "</strong></p>");
                        out.println("<p>Mensagem do servlet: " + (finalMessage != null && !finalMessage.isEmpty() ? finalMessage : "Nenhuma") + "</p>");
                        out.println("<p>Exceção: " + e.toString() + "</p>");
                        out.println("<h3>Stack Trace (para depuração):</h3><pre>");
                        e.printStackTrace(out);
                        out.println("</pre>");
                        out.println("<a href='principal.html'>Voltar para Principal</a>");
                        out.println("</body></html>");
                    } else {
                        System.err.println("Erro crítico no forward para " + paginaDestino + " mas a resposta já foi comitada. Exceção: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            } else {
                if (!response.isCommitted()) {
                    out.println("<!DOCTYPE html><html><head><title>Erro de Fluxo</title></head><body>");
                    out.println("<h1>Erro de Fluxo no Servlet</h1>");
                    out.println("<p>A página de destino não foi definida ou a operação não pôde ser concluída.</p>");
                    out.println("<p>Operação solicitada: " + (op != null ? op : "Nenhuma/Inválida") + "</p>");
                    out.println("<p>Mensagem do servlet: " + (finalMessage != null && !finalMessage.isEmpty() ? finalMessage : "Nenhuma") + "</p>");
                    out.println("<a href='principal.html'>Voltar para Principal</a>");
                    out.println("</body></html>");
                }
            }
        } 
    } 

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Servlet para controle de Produtos";
    }
}