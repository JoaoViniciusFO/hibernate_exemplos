

import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import entidades.Cidade;
import entidades.Endereco;
import entidades.Estado;
import entidades.Estado2;
import entidades.Usuario;


/*Tutorial seguido: https://javabeat.net/hibernate-maven-hsql/
 * 1 - inicie o sqlhdb antes de rodar essa classe. Para rodá-lo: vá na 
 * pasta do hsqldb/bin e rode o comando no promp: .\runManager.bat
 * 
 * Certifique-se que o nome do banco é o mesmo no hqsldb e no arquivo hibernate.cfg.xml.
 * 
 * NOTA: arquivos .hbm eram usados ANTES das annotations do hibernate. 
 * Fazer outro projeto, copia desse, COM as annotations do hibernate.
 * */
public class App {
	  public static void main(String[] args) {
	     System.out.println("Maven + Hibernate + HSQL");
	     Session session = HibernateUtil.getSessionFactory().openSession();
	     session.beginTransaction();
	     /*
	     Endereco endereco = new Endereco();
	     endereco.setLogradouro("Rua das conchas 666");
	     
	     Cidade cidade = new Cidade();
	     cidade.setNome("Goiania");
	     
	     Estado estado = new Estado();
	     estado.setSigla("GO");
	     
	     cidade.setEstado(estado);
	     
	     endereco.setCidade(cidade);
	     
	     Usuario usuario = new Usuario();
	     usuario.setNome("frederico6");
	     usuario.setEndereco(endereco);
	     */
	     //salvarGenerico(usuario);//só usuario sem endereço, ok.
	     
	     /*TEm que salvar o endereço primeiro, e depois o usuário. Senão,
	      * dá o erro de 'save the transient object before flushing*/
	     //salvarGenerico(usuario.getEndereco().getCidade().getEstado());
	     //salvarGenerico(usuario.getEndereco().getCidade());
	     //salvarGenerico(usuario.getEndereco());
	     //salvarGenerico(usuario);
	     //listContact();
	     
	     // Abaixo, exemplo de ManyToOne.
	     /*
	     Estado2 estado2 = new Estado2();
	     estado2.setSigla("DF");
	    
	     Cidade cidade1 = new Cidade();
	     cidade1.setNome("Brazlandia");
	     
	     salvarGenerico(cidade1);
	     
	     Cidade cidade2 = new Cidade();
	     cidade2.setNome("SetorOlandia");
	     
	     salvarGenerico(cidade2);
	     
	     estado2.setCidade(cidade1);
	     
	     salvarGenerico(estado2);
	     
	     estado2.setCidade(cidade2);
	     salvarGenerico(estado2);
	     listarEstado();
	     */
	     //fim do exemplo ManyToOne
	     
	     //exemplo de Criteria
	     Estado2 estado = new Estado2();
	     estado.setSigla("DF");
	     
	     Cidade cidade3 = new Cidade();
	     cidade3.setNome("Taguatinga");
	     salvarGenerico(cidade3);
	     
	     Cidade cidade4 = new Cidade();
	     cidade4.setNome("Ceilandia");
	     salvarGenerico(cidade4);
	     
	     Cidade cidade5 = new Cidade();
	     cidade5.setNome("Taguacenter");
	     salvarGenerico(cidade5);
	     
	     estado.setCidade(cidade3);
	     salvarGenerico(estado);
	     
	     //procurando por um atributo especifico
	     Criteria crit = session.createCriteria(Cidade.class);
	     crit.add(Restrictions.eq("nome","Taguatinga"));
	     List results = crit.list();
	     
	     //procurando por um pedaço de um atributo
	     crit = session.createCriteria(Cidade.class);
	     crit.add(Restrictions.like("nome","Tag%"));
	     List results2 = crit.list();
	     
	     //procurando por atributos nulos
	     crit = session.createCriteria(Cidade.class);
	     crit.add(Restrictions.isNull("estado"));
	     List results3 = crit.list();
	     
	     //um exemplo que usa o método ilike() para pesquisar combinações de case-insensitive no final da String.
	     /*
	      * ANYWHERE: Qualquer parte na String.
			END: Final da String.
			EXACT: Combinação exata.
			START: Inicio da String.
	      * */
	     crit = session.createCriteria(Cidade.class);
	     crit.add(Restrictions.ilike("nome","landia", MatchMode.END));
	     List results4 = crit.list();
	     
	     //pesquisando por ids menores que 30
	     /*
	      *  gt() (greater-than) que verifica se é maior que, 
	      *  ge() (greater-than-or-equal-to) que verifica se é maior que e igual, 
	      *  lt() (less-than) que verifica se é menor, 
	      *  le() (less-than-or-equal-to) que verifica se é menor e igual.
	      * */
	     crit = session.createCriteria(Cidade.class);
	     crit.add(Restrictions.gt("id",new Long(30L)));
	     List results5 = crit.list();
	     
	     crit.add(Restrictions.lt("id",new Long(30L)));
	     List results6 = crit.list();
	     
	     //Criando mais de uma restrição (equivalente a AND no SQL)
	     crit.add(Restrictions.gt("id",new Long(30L)));
	     crit.add(Restrictions.like("nome","Tag%"));
	     List results7 = crit.list();
	     
	     //Criando mais de uma restrição com OR
	     crit = session.createCriteria(Cidade.class);
	     Criterion id = Restrictions.gt("id",new Long(30L));
	     Criterion nome = Restrictions.like("nome","Tag%");
	     LogicalExpression orExp = Restrictions.or(id,nome);
	     crit.add(orExp);
	     List results8 = crit.list();
	     
	     //três OR ou mais podemos representar isso de uma forma melhor utilizado o objeto org.hibernate.criterion.Disjunction. 
	     crit = session.createCriteria(Cidade.class);
	     Criterion id2 = Restrictions.lt("id",new Long(30L));
	     Criterion nome2 = Restrictions.like("nome","Tag%");
	    // Criterion estado2 = Restrictions.ilike("estado","DF");
	     Disjunction disjunction = Restrictions.disjunction();
	     disjunction.add(id2);
	     disjunction.add(nome2);
	    // disjunction.add(estado2);
	     crit.add(disjunction); 
	     List results9 = crit.list();
	     
	     // sqlRestriction(). Essa restrição permite especificarmos um SQL diretamente na API Criteria.
	     crit = session.createCriteria(Cidade.class);
	     /*O {alias} é utilizado quando não sabemos o nome da tabela que a nossa classe utiliza. 
	      * Dessa forma, {alias} significa o nome da tabela da classe.
	      * */
	     crit.add(Restrictions.sqlRestriction("{alias}.nome like 'Cid%'"));
	     List results10 = crit.list();
	     
	     //Paginando resultados com o criteria
	     crit = session.createCriteria(Cidade.class);
	     crit.setFirstResult(1);
	     crit.setMaxResults(2);
	     List results11 = crit.list();
	     
	     //Retornando resultados único com uniqueResult().
	     crit = session.createCriteria(Cidade.class);
	     Criterion preco = Restrictions.lt("id",new Long(30L));
	     crit.setMaxResults(1);
	     Cidade cidade = (Cidade) crit.uniqueResult();
	     
	     //Ordenando resultados
	     crit = session.createCriteria(Cidade.class);
	     crit.add(Restrictions.lt("id",new Long(30L)));
	     crit.addOrder(Order.desc("nome"));
	     List resultado = crit.list();
	     
	     /*
	      * Para adicionar uma restrição na que classe que esta associada com Criteria, 
	      * precisamos criar outro objeto Criteria, 
	      * e passar o nome da propriedade da classe associada para o método createCriteria().
	      *  Dessa forma, teremos outro objeto Criteria.
	      * */
	     
	     crit = session.createCriteria(Cidade.class);
	     Criteria prdCrit = crit.createCriteria("estado");
	     prdCrit.add(Restrictions.lt("id",new Long(30L)));
	     List results12 = crit.list();
	     
	     //Projeção e agregação
	     /*
	      * Depois que temos um objeto Projection, adicione isto para um objeto Criteria 
	      * com o método setProjection(). 
	      * Quando um objeto Criteria executa, a lista contém referência a objetos que 
	      * podemos fazer um cast para o tipo apropriado.
	      * 
	      * Outras funçoes agregadas:
	      * 
	      * avg(String propertyName): Retorna a média do valor da propriedade.
			count(String propertyName): Conta o número de vezes que a propriedade ocorre.
			countDistinct(String propertyName): Conta o número de valores únicos que contém a propriedade.
			max(String propertyName): Calcula o valor máximo dos valores da propriedade.
			min(String propertyName): Calcula o valor mínimo dos valores da propriedade.
			sum(String propertyName): Calcula a soma total dos valores da propriedade.
	      * */
	     crit = session.createCriteria(Cidade.class);
	     crit.setProjection(Projections.rowCount());
	     List results13 = crit.list();
	     
	     //Também podemos ter uma lista de projeções:
	     crit = session.createCriteria(Cidade.class);
	     ProjectionList projList = Projections.projectionList();
	     projList.add(Projections.max("id"));
	     projList.add(Projections.min("id"));
	     projList.add(Projections.avg("id"));
	     projList.add(Projections.countDistinct("nome"));
	     crit.setProjection(projList);
	     List results14 = crit.list();
	     
	     /*
	      * Utilizando projeções também podemos retornar APENAS PROPRIEDADES individuais 
	      * ao invés de entidades. Para isso basta utilizarmos o método property() 
	      * na classe Projections. 
	      * */
	     crit = session.createCriteria(Cidade.class);
	     projList = Projections.projectionList();
	     projList.add(Projections.property("id"));
	     projList.add(Projections.property("nome"));
	     crit.setProjection(projList);
	     List results15 = crit.list();
	     /*
	      * Retornar propriedades também é melhor para a performance da aplicação, 
	      * pois com isso temos uma redução substancial no tráfego de dados na rede.
	      * */
	     
	     //agrupar resultados, semelhante a clausula GROUP BY no SQL.
	     crit = session.createCriteria(Cidade.class);
	     projList = Projections.projectionList();//ProjectionList
	     projList.add(Projections.groupProperty("nome"));
	     //projList.add(Projections.groupProperty("preco"));
	     crit.setProjection(projList);
	     List results16 = crit.list();
	     
	     //fonte: https://www.devmedia.com.br/hibernate-api-criteria-realizando-consultas/29627
	   }

	   

	   public static void listContact() {
	       Session session = HibernateUtil.getSessionFactory().openSession();
	       Transaction transaction = null;
	       try {
	           transaction = session.beginTransaction();
	           @SuppressWarnings("unchecked")
	           List<Usuario> lista = session.createQuery("from Usuario")
	               .list();
	           for (Usuario u : lista) {
	             
	               System.out.println(u.getNome());
	           }
	           transaction.commit();
	       } catch (HibernateException e) {
	           transaction.rollback();
	           e.printStackTrace();
	       } finally {
	           session.close();
	       }
	   }
	   
	   public static void listarEstado() {
	       Session session = HibernateUtil.getSessionFactory().openSession();
	       Transaction transaction = null;
	       try {
	           transaction = session.beginTransaction();
	           @SuppressWarnings("unchecked")
	           List<Estado2> lista = session.createQuery("from Estado2")
	               .list();
	           for (Estado2 u : lista) {
	             
	               System.out.println(u.getSigla());
	           }
	           transaction.commit();
	       } catch (HibernateException e) {
	           transaction.rollback();
	           e.printStackTrace();
	       } finally {
	           session.close();
	       }
	   }
	   
	   public static void salvarGenerico(Object o) {
		   Session session = HibernateUtil.getSessionFactory().openSession();
		      Long IdInserido = null;
		      Transaction transaction = null;
		      try {
		           transaction = session.beginTransaction();
		           IdInserido = (Long) session.save(o);
		           transaction.commit();
		       } catch (HibernateException e) {
		           transaction.rollback();
		           e.printStackTrace();
		       } finally {
		           session.close();
		       }
	   }

	}