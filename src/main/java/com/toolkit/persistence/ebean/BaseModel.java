/*
 * CopyRight (c) 2005-2012 GLOBE Co, Ltd. All rights reserved. 
 * Filename: Model.java 
 * Creator: qiaofeng Create-Date: 下午04:20:22
 */
package com.toolkit.persistence.ebean;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.OptimisticLockException;

import com.avaje.ebean.BackgroundExecutor;
import com.avaje.ebean.BeanState;
import com.avaje.ebean.CallableSql;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionFactory;
import com.avaje.ebean.Filter;
import com.avaje.ebean.InvalidValue;
import com.avaje.ebean.Query;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlUpdate;
import com.avaje.ebean.Transaction;
import com.avaje.ebean.TxCallable;
import com.avaje.ebean.TxIsolation;
import com.avaje.ebean.TxRunnable;
import com.avaje.ebean.TxScope;
import com.avaje.ebean.Update;
import com.avaje.ebean.ValuePair;
import com.avaje.ebean.annotation.CacheStrategy;
import com.avaje.ebean.cache.ServerCacheManager;
import com.avaje.ebean.text.csv.CsvReader;
import com.avaje.ebean.text.json.JsonContext;

/**
 * the Ebean abstract parent Bean class for Model
 * 
 * @author qiaofeng
 * @version $Id: Model, v 0.1 2012-11-2 下午04:20:22 Exp $
 */
public abstract class BaseModel {
	
	/**
	 * Get the EbeanServer for a given DataSource. If name is null this will
	 * return the 'default' EbeanServer.
	 * <p>
	 * This is provided to access EbeanServer for databases other than the
	 * 'default' database. EbeanServer also provides more control over
	 * transactions and the ability to use transactions created externally to
	 * Ebean.
	 * </p>
	 * 
	 * <pre class="code">
	 * // use the &quot;hr&quot; database
	 * EbeanServer	hrDatabase	= Ebean.getServer(&quot;hr&quot;);
	 * 													
	 * 													Person	person	= hrDatabase
	 * 																			.find(Person.class,
	 * 																					10);
	 * </pre>
	 * 
	 * @param name
	 *            the name of the server, use null for the 'default server'
	 */
	public final static EbeanServer getServer(String name) {
		return Ebean.getServer(name);
	}
	
	/**
	 * Return the ExpressionFactory from the default server.
	 * <p>
	 * The ExpressionFactory is used internally by the query and ExpressionList
	 * to build the WHERE and HAVING clauses. Alternatively you can use the
	 * ExpressionFactory directly to create expressions to add to the query
	 * where clause.
	 * </p>
	 * <p>
	 * Alternatively you can use the {@link Expr} as a shortcut to the
	 * ExpressionFactory of the 'Default' EbeanServer.
	 * </p>
	 * <p>
	 * You generally need to the an ExpressionFactory (or {@link Expr}) to build
	 * an expression that uses OR like Expression e = Expr.or(..., ...);
	 * </p>
	 */
	public final static ExpressionFactory getExpressionFactory() {
		return Ebean.getExpressionFactory();
	}
	
	/**
	 * Return the next identity value for a given bean type.
	 * <p>
	 * This will only work when a IdGenerator is on this bean type such as a DB
	 * sequence or UUID.
	 * </p>
	 * <p>
	 * For DB's supporting getGeneratedKeys and sequences such as Oracle10 you
	 * do not need to use this method generally. It is made available for more
	 * complex cases where it is useful to get an ID prior to some processing.
	 * </p>
	 */
	public final static Object nextId(Class<?> beanType) {
		return Ebean.nextId(beanType);
	}
	
	/**
	 * Log a comment to the transaction log of the current transaction.
	 * <p>
	 * If there is no current transaction this comment does not go anywhere.
	 * </p>
	 */
	public final static void logComment(String msg) {
		Ebean.logComment(msg);
	}
	
	/**
	 * Start a new explicit transaction.
	 * <p>
	 * The transaction is stored in a ThreadLocal variable and typically you
	 * only need to use the returned Transaction <em>IF</em> you wish to do
	 * things like use batch mode, change the transaction isolation level, use
	 * savepoints or log comments to the transaction log.
	 * </p>
	 * <p>
	 * Example of using a transaction to span multiple calls to find(), save()
	 * etc.
	 * </p>
	 * 
	 * <pre class="code">
	 * // start a transaction (stored in a ThreadLocal)
	 * Ebean.beginTransaction(); 
	 * try { 
	 * 	Order order = Ebean.find(Order.class,10); ...
	 * 
	 * 	Ebean.save(order);
	 * 
	 * 	Ebean.commitTransaction();
	 * 
	 * } public finally { 
	 * 	// rollback if we didn't commit 
	 * 	// i.e. an exception occurred before commitTransaction(). 
	 * 	Ebean.endTransaction(); 
	 * }
	 * </pre>
	 * 
	 * <p>
	 * If you want to externalise the transaction management then you should be
	 * able to do this via EbeanServer. Specifically with EbeanServer you can
	 * pass the transaction to the various find() and save() execute() methods.
	 * This gives you the ability to create the transactions yourself externally
	 * from Ebean and pass those transactions through to the various methods
	 * available on EbeanServer.
	 * </p>
	 */
	public final static Transaction beginTransaction() {
		return Ebean.beginTransaction();
	}
	
	/**
	 * Start a transaction additionally specifying the isolation level.
	 * 
	 * @param isolation
	 *            the Transaction isolation level
	 * 
	 */
	public final static Transaction beginTransaction(TxIsolation isolation) {
		return Ebean.beginTransaction(isolation);
	}
	
	/**
	 * Returns the current transaction or null if there is no current
	 * transaction in scope.
	 */
	public final static Transaction currentTransaction() {
		return Ebean.currentTransaction();
	}
	
	/**
	 * Commit the current transaction.
	 */
	public final static void commitTransaction() {
		Ebean.commitTransaction();
	}
	
	/**
	 * Rollback the current transaction.
	 */
	public final static void rollbackTransaction() {
		Ebean.rollbackTransaction();
	}
	
	/**
	 * If the current transaction has already been committed do nothing
	 * otherwise rollback the transaction.
	 * <p>
	 * Useful to put in a public finally block to ensure the transaction is
	 * ended, rather than a rollbackTransaction() in each catch block.
	 * </p>
	 * <p>
	 * Code example:
	 * </p>
	 * 
	 * <pre class="code">
	 * Ebean.beginTransaction();
	 * try {
	 * 	// do some fetching and or persisting
	 * 	// commit at the end Ebean.commitTransaction();
	 * 	
	 * } public finally {
	 * 	// if commit didn't occur then rollback the transaction
	 * 	Ebean.endTransaction();
	 * }
	 * </pre>
	 */
	public final static void endTransaction() {
		Ebean.endTransaction();
	}
	
	/**
	 * Validate a bean.
	 * <p>
	 * This will validate all of the properties on the bean in a recursive
	 * fashion. Typically if cascade save or delete is on then the validation
	 * will cascade those same associations.
	 * </p>
	 * <p>
	 * If no errors are detected then this returns null. Otherwise the returned
	 * InvalidValue holds the errors from all the rules tested. Use
	 * {@link InvalidValue#getErrors()} to get the list of errors that occurred.
	 * </p>
	 * 
	 * @return a InvalidValue holding the errors or null
	 */
	public final static InvalidValue validate(Object bean) {
		return Ebean.validate(bean);
	}
	
	/**
	 * Validate a bean property.
	 * <p>
	 * If value passed in is null, then the property value from the bean is
	 * used.
	 * </p>
	 * <p>
	 * If no errors are detected an empty array is returned.
	 * </p>
	 * 
	 * @param bean
	 *            the bean used if value is null
	 * @param propertyName
	 *            the property to validate
	 * @param value
	 *            the value to validate. If this is null then the value from the
	 *            bean is used to validate.
	 * @return a InvalidValue holding the errors for this property (returns an
	 *         empty array if there are no errors).
	 */
	public final static InvalidValue[] validate(Object bean,
			String propertyName, Object value) {
		return Ebean.validate(bean, propertyName, value);
	}
	
	/**
	 * Return a map of the differences between two objects of the same type.
	 * <p>
	 * When null is passed in for b, then the 'OldValues' of a is used for the
	 * difference comparison.
	 * </p>
	 */
	public final static Map<String, ValuePair> diff(Object a, Object b) {
		return Ebean.diff(a, b);
	}
	
	/**
	 * Either Insert or Update the bean depending on its state.
	 * <p>
	 * If there is no current transaction one will be created and committed for
	 * you automatically.
	 * </p>
	 * <p>
	 * Save can cascade along relationships. For this to happen you need to
	 * specify a cascade of CascadeType.ALL or CascadeType.PERSIST on the
	 * OneToMany, OneToOne or ManyToMany annotation.
	 * </p>
	 * <p>
	 * In this example below the details property has a CascadeType.ALL set so
	 * saving an order will also save all its details.
	 * </p>
	 * 
	 * <pre class="code">
	 * public final class Order { ...
	 * 	
	 * 	&#064;OneToMany(cascade=CascadeType.ALL, mappedBy=&quot;order&quot;)
	 * 	&#064;JoinColumn(name=&quot;order_id&quot;) 
	 * 	List&lt;OrderDetail&gt; details; 
	 * 	... 
	 * }
	 * </pre>
	 * 
	 * <p>
	 * When a save cascades via a OneToMany or ManyToMany Ebean will
	 * automatically set the 'parent' object to the 'detail' object. In the
	 * example below in saving the order and cascade saving the order details
	 * the 'parent' order will be set against each order detail when it is
	 * saved.
	 * </p>
	 */
	public final static void save(Object bean) throws OptimisticLockException {
		Ebean.save(bean);
	}
	
	/**
	 * Force an update using the bean updating the non-null properties.
	 * <p>
	 * You can use this method to FORCE an update to occur (even on a bean that
	 * has not been fetched but say built from JSON or XML). When
	 * {@link Ebean#save(Object)} is used Ebean determines whether to use an
	 * insert or an update based on the state of the bean. Using this method
	 * will force an update to occur.
	 * </p>
	 * <p>
	 * It is expected that this method is most useful in stateless REST services
	 * or web applications where you have the values you wish to update but no
	 * existing bean.
	 * </p>
	 * <p>
	 * For updates against beans that have not been fetched (say built from JSON
	 * or XML) this will treat deleteMissingChildren=true and will delete any
	 * 'missing children'. Refer to
	 * {@link EbeanServer#update(Object, java.util.Set, Transaction, boolean, boolean)}
	 * .
	 * </p>
	 * 
	 * <pre class="code">
	 * 
	 * Customer c = new Customer();
	 * c.setId(7);
	 * c.setName(&quot;ModifiedNameNoOCC&quot;);
	 * 
	 * // generally you should set the version property
	 * // so that Optimistic Concurrency Checking is used.
	 * // If a version property is not set then no Optimistic
	 * // Concurrency Checking occurs for the update
	 * // c.setLastUpdate(lastUpdateTime);
	 * 
	 * // by default the Non-null properties
	 * // are included in the update
	 * Ebean.update(c);
	 * 
	 * </pre>
	 */
	public final static void update(Object bean) {
		Ebean.update(bean);
	}
	
	/**
	 * Force an update using the bean explicitly stating the properties to
	 * update.
	 * <p>
	 * If you don't specify explicit properties to use in the update then the
	 * non-null properties are included in the update.
	 * </p>
	 * <p>
	 * For updates against beans that have not been fetched (say built from JSON
	 * or XML) this will treat deleteMissingChildren=true and will delete any
	 * 'missing children'. Refer to
	 * {@link EbeanServer#update(Object, java.util.Set, Transaction, boolean, boolean)}
	 * .
	 * </p>
	 * 
	 * @param bean
	 *            The bean holding the values to be included in the update.
	 * @param updateProps
	 *            the explicit set of properties to include in the update (can
	 *            be null).
	 */
	public final static void update(Object bean, Set<String> updateProps) {
		Ebean.update(bean, updateProps);
	}
	
	/**
	 * Save all the beans from an Iterator.
	 */
	public final static int save(Iterator<?> iterator)
			throws OptimisticLockException {
		return Ebean.save(iterator);
	}
	
	/**
	 * Save all the beans from a Collection.
	 */
	public final static int save(Collection<?> c)
			throws OptimisticLockException {
		return save(c.iterator());
	}
	
	/**
	 * Delete the associations (from the intersection table) of a ManyToMany
	 * given the owner bean and the propertyName of the ManyToMany collection.
	 * <p>
	 * Typically these deletions occur automatically when persisting a
	 * ManyToMany collection and this provides a way to invoke those deletions
	 * directly.
	 * </p>
	 * 
	 * @return the number of associations deleted (from the intersection table).
	 */
	public final static int deleteManyToManyAssociations(Object ownerBean,
			String propertyName) {
		return Ebean.deleteManyToManyAssociations(ownerBean, propertyName);
	}
	
	/**
	 * Save the associations of a ManyToMany given the owner bean and the
	 * propertyName of the ManyToMany collection.
	 * <p>
	 * Typically the saving of these associations (inserting into the
	 * intersection table) occurs automatically when persisting a ManyToMany.
	 * This provides a way to invoke those insertions directly.
	 * </p>
	 * <p>
	 * You can use this when the collection is new and in this case all the
	 * entries in the collection are treated as additions are result in inserts
	 * into the intersection table.
	 * </p>
	 */
	public final static void saveManyToManyAssociations(Object ownerBean,
			String propertyName) {
		Ebean.saveManyToManyAssociations(ownerBean, propertyName);
	}
	
	/**
	 * Save the associated collection or bean given the property name.
	 * <p>
	 * This is similar to performing a save cascade on a specific property
	 * manually/programmatically.
	 * </p>
	 * <p>
	 * Note that you can turn on/off cascading for a transaction via
	 * {@link Transaction#setPersistCascade(boolean)}
	 * </p>
	 * 
	 * @param ownerBean
	 *            the bean instance holding the property we want to save
	 * @param propertyName
	 *            the property we want to save
	 */
	public final static void saveAssociation(Object ownerBean,
			String propertyName) {
		Ebean.saveAssociation(ownerBean, propertyName);
	}
	
	/**
	 * Delete the bean.
	 * <p>
	 * If there is no current transaction one will be created and committed for
	 * you automatically.
	 * </p>
	 */
	public final static void delete(Object bean) throws OptimisticLockException {
		Ebean.delete(bean);
	}
	
	/**
	 * Delete the bean given its type and id.
	 */
	public final static int delete(Class<?> beanType, Object id) {
		return Ebean.delete(beanType, id);
	}
	
	/**
	 * Delete several beans given their type and id values.
	 */
	public final static void delete(Class<?> beanType, Collection<?> ids) {
		Ebean.delete(beanType, ids);
	}
	
	/**
	 * Delete all the beans from an Iterator.
	 */
	public final static int delete(Iterator<?> it)
			throws OptimisticLockException {
		return Ebean.delete(it);
	}
	
	/**
	 * Delete all the beans from a Collection.
	 */
	public final static int delete(Collection<?> c)
			throws OptimisticLockException {
		return delete(c.iterator());
	}
	
	/**
	 * Refresh the values of a bean.
	 * <p>
	 * Note that this does not refresh any OneToMany or ManyToMany properties.
	 * </p>
	 */
	public final static void refresh(Object bean) {
		Ebean.refresh(bean);
	}
	
	/**
	 * Refresh a 'many' property of a bean.
	 * 
	 * <pre class="code">
	 * Order order = ...;
	 * ...
	 * // refresh the order details...
	 * Ebean.refreshMany(order, &quot;details&quot;);
	 * </pre>
	 * 
	 * @param bean
	 *            the entity bean containing the List Set or Map to refresh.
	 * @param manyPropertyName
	 *            the property name of the List Set or Map to refresh.
	 */
	public final static void refreshMany(Object bean, String manyPropertyName) {
		Ebean.refreshMany(bean, manyPropertyName);
	}
	
	/**
	 * Get a reference object.
	 * <p>
	 * This is sometimes described as a proxy (with lazy loading).
	 * </p>
	 * 
	 * <pre class="code">
	 * Product	product	= Ebean.getReference(Product.class, 1);
	 * 														
	 * 														// You can get the id
	 * 														// without causing a
	 * 														// fetch/lazy load
	 * 														Integer	productId	= product
	 * 																					.getId();
	 * 																								
	 * 																								// If
	 * 																								// you
	 * 																								// try
	 * 																								// to
	 * 																								// get
	 * 																								// any
	 * 																								// other
	 * 																								// property
	 * 																								// a
	 * 																								// fetch/lazy
	 * 																								// loading
	 * 																								// will
	 * 																								// occur
	 * 																								// This
	 * 																								// will
	 * 																								// cause
	 * 																								// a
	 * 																								// query
	 * 																								// to
	 * 																								// execute...
	 * 																								String	name	= product
	 * 																														.getName();
	 * </pre>
	 * 
	 * @param beanType
	 *            the type of entity bean
	 * @param id
	 *            the id value
	 */
	public final static <T> T getReference(Class<T> beanType, Object id) {
		return Ebean.getReference(beanType, id);
	}
	
	/**
	 * Sort the list using the sortByClause which can contain a comma delimited
	 * list of property names and keywords asc, desc, nullsHigh and nullsLow.
	 * <ul>
	 * <li>asc - ascending order (which is the default)</li>
	 * <li>desc - Descending order</li>
	 * <li>nullsHigh - Treat null values as high/large values (which is the
	 * default)</li>
	 * <li>nullsLow- Treat null values as low/very small values</li>
	 * </ul>
	 * <p>
	 * If you leave off any keywords the defaults are ascending order and
	 * treating nulls as high values.
	 * </p>
	 * <p>
	 * Note that the sorting uses a Comparator and Collections.sort(); and does
	 * not invoke a DB query.
	 * </p>
	 * 
	 * <pre class="code">
	 * 
	 * // find orders and their customers
	 * List&lt;Order&gt; list = Ebean.find(Order.class).fetch(&quot;customer&quot;).orderBy(&quot;id&quot;)
	 * 		.findList();
	 * 
	 * // sort by customer name ascending, then by order shipDate
	 * // ... then by the order status descending
	 * Ebean.sort(list, &quot;customer.name, shipDate, status desc&quot;);
	 * 
	 * // sort by customer name descending (with nulls low)
	 * // ... then by the order id
	 * Ebean.sort(list, &quot;customer.name desc nullsLow, id&quot;);
	 * 
	 * </pre>
	 * 
	 * @param list
	 *            the list of entity beans
	 * @param sortByClause
	 *            the properties to sort the list by
	 */
	public final static <T extends BaseModel> void sort(List<T> list,
			String sortByClause) {
		Ebean.sort(list, sortByClause);
	}
	
	/**
	 * Find a bean using its unique id. This will not use caching.
	 * 
	 * <pre class="code">
	 * // Fetch order 1
	 * Order	order	= Ebean.find(Order.class, 1);
	 * </pre>
	 * 
	 * <p>
	 * If you want more control over the query then you can use createQuery()
	 * and Query.findUnique();
	 * </p>
	 * 
	 * <pre class="code">
	 * // ... additionally fetching customer, customer shipping address,
	 * // order details, and the product associated with each order detail.
	 * // note: only product id and name is fetch (its a &quot;partial object&quot;).
	 * // note: all other objects use &quot;*&quot; and have all their properties fetched.
	 * 
	 * Query&lt;Order&gt; query = Ebean.createQuery(Order.class);
	 * query.setId(1);
	 * query.fetch(&quot;customer&quot;);
	 * query.fetch(&quot;customer.shippingAddress&quot;);
	 * query.fetch(&quot;details&quot;);
	 * 
	 * // fetch associated products but only fetch their product id and name
	 * query.fetch(&quot;details.product&quot;, &quot;name&quot;);
	 * 
	 * // traverse the object graph...
	 * 
	 * Order order = query.findUnique();
	 * Customer customer = order.getCustomer();
	 * Address shippingAddress = customer.getShippingAddress();
	 * List&lt;OrderDetail&gt; details = order.getDetails();
	 * OrderDetail detail0 = details.get(0);
	 * Product product = detail0.getProduct();
	 * String productName = product.getName();
	 * </pre>
	 * 
	 * @param beanType
	 *            the type of entity bean to fetch
	 * @param id
	 *            the id value
	 */
	public final static <T extends BaseModel> T find(Class<T> beanType,
			Object id) {
		return Ebean.find(beanType, id);
	}
	
	public final static <T extends BaseModel> T findById(Class<T> beanType,
			Object id) {
		return Ebean.find(beanType, id);
	}
	
	/**
	 * Create a <a href="SqlQuery.html">SqlQuery</a> for executing native sql
	 * query statements.
	 * <p>
	 * Note that you can use raw SQL with entity beans, refer to the SqlSelect
	 * annotation for examples.
	 * </p>
	 */
	public final static SqlQuery createSqlQuery(String sql) {
		return Ebean.createSqlQuery(sql);
	}
	
	/**
	 * Create a named sql query.
	 * <p>
	 * The query statement will be defined in a deployment orm xml file.
	 * </p>
	 * 
	 * @param namedQuery
	 *            the name of the query
	 */
	public final static SqlQuery createNamedSqlQuery(String namedQuery) {
		return Ebean.createNamedSqlQuery(namedQuery);
	}
	
	/**
	 * Create a sql update for executing native dml statements.
	 * <p>
	 * Use this to execute a Insert Update or Delete statement. The statement
	 * will be native to the database and contain database table and column
	 * names.
	 * </p>
	 * <p>
	 * See {@link SqlUpdate} for example usage.
	 * </p>
	 * <p>
	 * Where possible it would be expected practice to put the statement in a
	 * orm xml file (named update) and use {@link #createNamedSqlUpdate(String)}
	 * .
	 * </p>
	 */
	public final static SqlUpdate createSqlUpdate(String sql) {
		return Ebean.createSqlUpdate(sql);
	}
	
	/**
	 * Create a CallableSql to execute a given stored procedure.
	 * 
	 * @see CallableSql
	 */
	public final static CallableSql createCallableSql(String sql) {
		return Ebean.createCallableSql(sql);
	}
	
	/**
	 * Create a named sql update.
	 * <p>
	 * The statement (an Insert Update or Delete statement) will be defined in a
	 * deployment orm xml file.
	 * </p>
	 * 
	 * <pre class="code">
	 * // Use a namedQuery
	 * UpdateSql update = Ebean.createNamedSqlUpdate(&quot;update.topic.count&quot;);
	 * 
	 * update.setParameter(&quot;count&quot;, 1);
	 * update.setParameter(&quot;topicId&quot;, 50);
	 * 
	 * int modifiedCount = update.execute();
	 * </pre>
	 */
	public final static SqlUpdate createNamedSqlUpdate(String namedQuery) {
		return Ebean.createNamedSqlUpdate(namedQuery);
	}
	
	/**
	 * Return a named Query that will have defined fetch paths, predicates etc.
	 * <p>
	 * The query is created from a statement that will be defined in a
	 * deployment orm xml file or NamedQuery annotations. The query will
	 * typically already define fetch paths, predicates, order by clauses etc so
	 * often you will just need to bind required parameters and then execute the
	 * query.
	 * </p>
	 * 
	 * <pre class="code">
	 * // example
	 * Query&lt;Order&gt; query = Ebean.createNamedQuery(Order.class, &quot;new.for.customer&quot;);
	 * query.setParameter(&quot;customerId&quot;, 23);
	 * List&lt;Order&gt; newOrders = query.findList();
	 * </pre>
	 * 
	 * @param beanType
	 *            the class of entity to be fetched
	 * @param namedQuery
	 *            the name of the query
	 */
	public final static <T extends BaseModel> Query<T> createNamedQuery(
			Class<T> beanType, String namedQuery) {
		
		return Ebean.createNamedQuery(beanType, namedQuery);
	}
	
	/**
	 * Create a query using the query language.
	 * <p>
	 * Note that you are allowed to add additional clauses using where() as well
	 * as use fetch() and setOrderBy() after the query has been created.
	 * </p>
	 * <p>
	 * Note that this method signature used to map to named queries and that has
	 * moved to {@link #createNamedQuery(Class, String)}.
	 * </p>
	 * 
	 * <pre class="code">
	 * String	q	= &quot;find order fetch details where status = :st&quot;;
	 * 																
	 * 																List&lt;Order&gt;	newOrders	= Ebean.createQuery(
	 * 																								Order.class,
	 * 																								q)
	 * 																								.setParameter(
	 * 																										&quot;st&quot;,
	 * 																										Order.Status.NEW)
	 * 																								.findList();
	 * </pre>
	 * 
	 * @param query
	 *            the object query
	 */
	public final static <T extends BaseModel> Query<T> createQuery(
			Class<T> beanType, String query) {
		return Ebean.createQuery(beanType, query);
	}
	
	/**
	 * Create a named orm update. The update statement is specified via the
	 * NamedUpdate annotation.
	 * <p>
	 * The orm update differs from the SqlUpdate in that it uses the bean name
	 * and bean property names rather than table and column names.
	 * </p>
	 * <p>
	 * Note that named update statements can be specified in raw sql (with
	 * column and table names) or using bean name and bean property names. This
	 * can be specified with the isSql flag.
	 * </p>
	 * <p>
	 * Example named updates:
	 * </p>
	 * 
	 * <pre class="code">
	 * package app.data;
	 * 
	 * import ...
	 * 
	 * &#064;NamedUpdates(value = { 
	 * 	&#064;NamedUpdate( name = &quot;setTitle&quot;, 
	 * 		isSql = false, 
	 * 		notifyCache = false, 
	 * 		update = &quot;update topic set title = :title, postCount = :postCount where id = :id&quot;), 
	 * 	&#064;NamedUpdate( name = &quot;setPostCount&quot;,
	 * 		notifyCache = false,
	 * 		update = &quot;update f_topic set post_count = :postCount where id = :id&quot;), 
	 * 	&#064;NamedUpdate( name = &quot;incrementPostCount&quot;, 
	 * 		notifyCache = false, 
	 * 		isSql = false,
	 * 		update = &quot;update Topic set postCount = postCount + 1 where id = :id&quot;) }) 
	 * &#064;Entity 
	 * &#064;Table(name = &quot;f_topic&quot;) 
	 * public final class Topic { ...
	 * </pre>
	 * 
	 * <p>
	 * Example using a named update:
	 * </p>
	 * 
	 * <pre class="code">
	 * Update&lt;Topic&gt; update = Ebean.createNamedUpdate(Topic.class, &quot;setPostCount&quot;);
	 * update.setParameter(&quot;postCount&quot;, 10);
	 * update.setParameter(&quot;id&quot;, 3);
	 * 
	 * int rows = update.execute();
	 * System.out.println(&quot;rows updated: &quot; + rows);
	 * </pre>
	 */
	public final static <T extends BaseModel> Update<T> createNamedUpdate(
			Class<T> beanType, String namedUpdate) {
		
		return Ebean.createNamedUpdate(beanType, namedUpdate);
	}
	
	/**
	 * Create a orm update where you will supply the insert/update or delete
	 * statement (rather than using a named one that is already defined using
	 * the &#064;NamedUpdates annotation).
	 * <p>
	 * The orm update differs from the sql update in that it you can use the
	 * bean name and bean property names rather than table and column names.
	 * </p>
	 * <p>
	 * An example:
	 * </p>
	 * 
	 * <pre class="code">
	 * 
	 * // The bean name and properties - &quot;topic&quot;,&quot;postCount&quot; and &quot;id&quot;
	 * 
	 * // will be converted into their associated table and column names
	 * String updStatement = &quot;update topic set postCount = :pc where id = :id&quot;;
	 * 
	 * Update&lt;Topic&gt; update = Ebean.createUpdate(Topic.class, updStatement);
	 * 
	 * update.set(&quot;pc&quot;, 9);
	 * update.set(&quot;id&quot;, 3);
	 * 
	 * int rows = update.execute();
	 * System.out.println(&quot;rows updated:&quot; + rows);
	 * </pre>
	 */
	public final static <T extends BaseModel> Update<T> createUpdate(
			Class<T> beanType, String ormUpdate) {
		
		return Ebean.createUpdate(beanType, ormUpdate);
	}
	
	/**
	 * Create a CsvReader for a given beanType.
	 */
	public final static <T extends BaseModel> CsvReader<T> createCsvReader(
			Class<T> beanType) {
		
		return Ebean.createCsvReader(beanType);
	}
	
	/**
	 * Create a query for a type of entity bean.
	 * <p>
	 * You can use the methods on the Query object to specify fetch paths,
	 * predicates, order by, limits etc.
	 * </p>
	 * <p>
	 * You then use findList(), findSet(), findMap() and findUnique() to execute
	 * the query and return the collection or bean.
	 * </p>
	 * <p>
	 * Note that a query executed by {@link Query#findList()}
	 * {@link Query#findSet()} etc will execute against the same EbeanServer
	 * from which is was created.
	 * </p>
	 * 
	 * <pre class="code">
	 * // Find order 2 additionally fetching the customer, details and details.product
	 * // name.
	 * 
	 * Query&lt;Order&gt; query = Ebean.createQuery(Order.class);
	 * query.fetch(&quot;customer&quot;);
	 * query.fetch(&quot;details&quot;);
	 * query.fetch(&quot;detail.product&quot;, &quot;name&quot;);
	 * query.setId(2);
	 * 
	 * Order order = query.findUnique();
	 * 
	 * // Find order 2 additionally fetching the customer, details and details.product
	 * // name.
	 * // Note: same query as above but using the query language
	 * // Note: using a named query would be preferred practice
	 * 
	 * String oql = &quot;find order fetch customer fetch details fetch details.product (name) where id = :orderId &quot;;
	 * 
	 * Query&lt;Order&gt; query = Ebean.createQuery(Order.class);
	 * query.setQuery(oql);
	 * query.setParameter(&quot;orderId&quot;, 2);
	 * 
	 * Order order = query.findUnique();
	 * 
	 * // Using a named query
	 * Query&lt;Order&gt; query = Ebean.createQuery(Order.class, &quot;with.details&quot;);
	 * query.setParameter(&quot;orderId&quot;, 2);
	 * 
	 * Order order = query.findUnique();
	 * 
	 * </pre>
	 * 
	 * @param beanType
	 *            the class of entity to be fetched
	 * @return A ORM Query object for this beanType
	 */
	public final static <T extends BaseModel> Query<T> createQuery(
			Class<T> beanType) {
		
		return Ebean.createQuery(beanType);
	}
	
	/**
	 * Create a query for a type of entity bean.
	 * <p>
	 * This is actually the same as {@link #createQuery(Class)}. The reason it
	 * exists is that people used to JPA will probably be looking for a
	 * createQuery method (the same as entityManager).
	 * </p>
	 * 
	 * @param beanType
	 *            the type of entity bean to find
	 * @return A ORM Query object for this beanType
	 */
	public final static <T extends BaseModel> Query<T> find(Class<T> beanType) {
		
		return Ebean.find(beanType);
	}
	
	/**
	 * Create a filter for sorting and filtering lists of entities locally
	 * without going back to the database.
	 * <p>
	 * This produces and returns a new list with the sort and filters applied.
	 * </p>
	 * <p>
	 * Refer to {@link Filter} for an example of its use.
	 * </p>
	 */
	public final static <T extends BaseModel> Filter<T> filter(Class<T> beanType) {
		return Ebean.filter(beanType);
	}
	
	/**
	 * Execute a Sql Update Delete or Insert statement. This returns the number
	 * of rows that where updated, deleted or inserted. If is executed in batch
	 * then this returns -1. You can get the actual rowCount after commit() from
	 * updateSql.getRowCount().
	 * <p>
	 * If you wish to execute a Sql Select natively then you should use the
	 * FindByNativeSql object.
	 * </p>
	 * <p>
	 * Note that the table modification information is automatically deduced and
	 * you do not need to call the Ebean.externalModification() method when you
	 * use this method.
	 * </p>
	 * <p>
	 * Example:
	 * </p>
	 * 
	 * <pre class="code">
	 * // example that uses 'named' parameters 
	 * String s = &quot;UPDATE f_topic set post_count = :count where id = :id&quot;
	 * 
	 * SqlUpdate update = Ebean.createSqlUpdate(s);
	 * 
	 * update.setParameter(&quot;id&quot;, 1);
	 * update.setParameter(&quot;count&quot;, 50);
	 * 
	 * int modifiedCount = Ebean.execute(update);
	 * 
	 * String msg = &quot;There where &quot; + modifiedCount + &quot;rows updated&quot;;
	 * </pre>
	 * 
	 * @param sqlUpdate
	 *            the update sql potentially with bind values
	 * 
	 * @return the number of rows updated or deleted. -1 if executed in batch.
	 * 
	 * @see SqlUpdate
	 * @see CallableSql
	 * @see Ebean#execute(CallableSql)
	 */
	public final static int execute(SqlUpdate sqlUpdate) {
		return Ebean.execute(sqlUpdate);
	}
	
	/**
	 * For making calls to stored procedures.
	 * <p>
	 * Example:
	 * </p>
	 * 
	 * <pre class="code">
	 * String sql = &quot;{call sp_order_modify(?,?,?)}&quot;;
	 * 
	 * CallableSql cs = Ebean.createCallableSql(sql);
	 * cs.setParameter(1, 27);
	 * cs.setParameter(2, &quot;SHIPPED&quot;);
	 * cs.registerOut(3, Types.INTEGER);
	 * 
	 * Ebean.execute(cs);
	 * 
	 * // read the out parameter
	 * Integer returnValue = (Integer) cs.getObject(3);
	 * </pre>
	 * 
	 * @see CallableSql
	 * @see Ebean#execute(SqlUpdate)
	 */
	public final static int execute(CallableSql callableSql) {
		return Ebean.execute(callableSql);
	}
	
	/**
	 * Execute a TxRunnable in a Transaction with an explicit scope.
	 * <p>
	 * The scope can control the transaction type, isolation and rollback
	 * semantics.
	 * </p>
	 * 
	 * <pre class="code">
	 * // set specific transactional scope settings 
	 * TxScope scope = TxScope.requiresNew().setIsolation(TxIsolation.SERIALIZABLE);
	 * 
	 * Ebean.execute(scope, new TxRunnable() { 
	 * 	public final void run() { 
	 * 		User u1 = Ebean.find(User.class, 1); 
	 * 		...
	 * 
	 * 	} 
	 * });
	 * </pre>
	 */
	public final static void execute(TxScope scope, TxRunnable r) {
		Ebean.execute(scope, r);
	}
	
	/**
	 * Execute a TxRunnable in a Transaction with the default scope.
	 * <p>
	 * The default scope runs with REQUIRED and by default will rollback on any
	 * exception (checked or runtime).
	 * </p>
	 * 
	 * <pre class="code">
	 * Ebean.execute(new TxRunnable() {
	 * 	public final void run() {
	 * 		User u1 = Ebean.find(User.class, 1);
	 * 		User u2 = Ebean.find(User.class, 2);
	 * 		
	 * 		u1.setName(&quot;u1 mod&quot;);
	 * 		u2.setName(&quot;u2 mod&quot;);
	 * 		
	 * 		Ebean.save(u1);
	 * 		Ebean.save(u2);
	 * 	}
	 * });
	 * </pre>
	 */
	public final static void execute(TxRunnable r) {
		Ebean.execute(r);
	}
	
	/**
	 * Execute a TxCallable in a Transaction with an explicit scope.
	 * <p>
	 * The scope can control the transaction type, isolation and rollback
	 * semantics.
	 * </p>
	 * 
	 * <pre class="code">
	 * // set specific transactional scope settings 
	 * TxScope scope = TxScope.requiresNew().setIsolation(TxIsolation.SERIALIZABLE);
	 * 
	 * Ebean.execute(scope, new TxCallable&lt;String&gt;() {
	 * 	public final String call() { 
	 * 		User u1 = Ebean.find(User.class, 1); 
	 * 		...
	 * 		return u1.getEmail(); 
	 * 	} 
	 * });
	 * </pre>
	 * 
	 */
	public final static <T extends BaseModel> T execute(TxScope scope,
			TxCallable<T> c) {
		return Ebean.execute(scope, c);
	}
	
	/**
	 * Execute a TxCallable in a Transaction with the default scope.
	 * <p>
	 * The default scope runs with REQUIRED and by default will rollback on any
	 * exception (checked or runtime).
	 * </p>
	 * <p>
	 * This is basically the same as TxRunnable except that it returns an Object
	 * (and you specify the return type via generics).
	 * </p>
	 * 
	 * <pre class="code">
	 * Ebean.execute(new TxCallable&lt;String&gt;() {
	 * 	public final String call() {
	 * 		User u1 = Ebean.find(User.class, 1);
	 * 		User u2 = Ebean.find(User.class, 2);
	 * 		
	 * 		u1.setName(&quot;u1 mod&quot;);
	 * 		u2.setName(&quot;u2 mod&quot;);
	 * 		
	 * 		Ebean.save(u1);
	 * 		Ebean.save(u2);
	 * 		
	 * 		return u1.getEmail();
	 * 	}
	 * });
	 * </pre>
	 */
	public final static <T extends BaseModel> T execute(TxCallable<T> c) {
		return Ebean.execute(c);
	}
	
	/**
	 * Inform Ebean that tables have been modified externally. These could be
	 * the result of from calling a stored procedure, other JDBC calls or
	 * external programs including other frameworks.
	 * <p>
	 * If you use Ebean.execute(UpdateSql) then the table modification
	 * information is automatically deduced and you do not need to call this
	 * method yourself.
	 * </p>
	 * <p>
	 * This information is used to invalidate objects out of the cache and
	 * potentially text indexes. This information is also automatically
	 * broadcast across the cluster.
	 * </p>
	 * <p>
	 * If there is a transaction then this information is placed into the
	 * current transactions event information. When the transaction is commited
	 * this information is registered (with the transaction manager). If this
	 * transaction is rolled back then none of the transaction event information
	 * registers including the information you put in via this method.
	 * </p>
	 * <p>
	 * If there is NO current transaction when you call this method then this
	 * information is registered immediately (with the transaction manager).
	 * </p>
	 * 
	 * @param tableName
	 *            the name of the table that was modified
	 * @param inserts
	 *            true if rows where inserted into the table
	 * @param updates
	 *            true if rows on the table where updated
	 * @param deletes
	 *            true if rows on the table where deleted
	 */
	public final static void externalModification(String tableName,
			boolean inserts, boolean updates, boolean deletes) {
		
		Ebean.externalModification(tableName, inserts, updates, deletes);
	}
	
	/**
	 * Return the BeanState for a given entity bean.
	 * <p>
	 * This will return null if the bean is not an enhanced (or subclassed)
	 * entity bean.
	 * </p>
	 */
	public final static BeanState getBeanState(Object bean) {
		return Ebean.getBeanState(bean);
	}
	
	/**
	 * Return the manager of the server cache ("L2" cache).
	 * 
	 */
	public final static ServerCacheManager getServerCacheManager() {
		return Ebean.getServerCacheManager();
	}
	
	/**
	 * Return the BackgroundExecutor service for asynchronous processing of
	 * queries.
	 */
	public final static BackgroundExecutor getBackgroundExecutor() {
		return Ebean.getBackgroundExecutor();
	}
	
	/**
	 * Run the cache warming queries on all bean types that have one defined for
	 * the default/primary EbeanServer.
	 * <p>
	 * A cache warming query can be defined via {@link CacheStrategy}.
	 * </p>
	 */
	public final static void runCacheWarming() {
		Ebean.runCacheWarming();
	}
	
	/**
	 * Run the cache warming query for a specific bean type for the
	 * default/primary EbeanServer.
	 * <p>
	 * A cache warming query can be defined via {@link CacheStrategy}.
	 * </p>
	 */
	public final static void runCacheWarming(Class<?> beanType) {
		
		Ebean.runCacheWarming(beanType);
	}
	
	/**
	 * Create a JsonContext that will use the default configuration options.
	 */
	public final static JsonContext createJsonContext() {
		return Ebean.createJsonContext();
	}
	
}
