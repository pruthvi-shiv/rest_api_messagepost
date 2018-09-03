package com.example.echo;

import static com.example.echo.Persistence.getDatastore;
import static com.example.echo.Persistence.getKeyFactory;
import static com.google.cloud.datastore.StructuredQuery.OrderBy.desc;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.EntityQuery;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.FullEntity.Builder;
import com.google.cloud.datastore.IncompleteKey;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

public class Mpost {

	public static Key key;
	private static KeyFactory keyFactory = getKeyFactory(Mpost.class);
	public String title;
	public String authorEmail;
	public String content;
	public Date date;

	public static KeyFactory getKeyfactory() {
		return keyFactory;
	}

	public static void setKeyfactory(KeyFactory keyfactory) {
		keyFactory = keyfactory;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthorEmail() {
		return authorEmail;
	}

	public void setAuthorEmail(String authorEmail) {
		this.authorEmail = authorEmail;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public static void setKey(Key key) {
		Mpost.key = key;
	}

	public static Key getKey() {
		return key;
	}

	public Mpost() {
		this.date = new Date();
	}

	public Mpost(String title, String content, String email) {
		this.date = new Date();
		this.title = title;
		this.content = content;
		authorEmail = email;
		key = keyFactory.newKey(this.hashCode());
	}

	public Mpost(Entity entity) {
		key = entity.hasKey() ? entity.getKey() : null;
		title = entity.contains("title") ? entity.getString("title") : null;
		authorEmail = entity.contains("authorEmail") ? entity.getString("authorEmail") : null;
		date = entity.contains("date") ? entity.getTimestamp("date").toSqlTimestamp() : null;
		content = entity.contains("content") ? entity.getString("content") : null;
	}

	public void save() {
		if (key == null) {
			key = keyFactory.newKey(this.hashCode());
		}

		System.out.println(this.authorEmail + this.content + this.title );

		Builder<Key> builder = FullEntity.newBuilder(key);

		if (authorEmail != null) {
			builder.set("authorEmail", authorEmail);
		}
		builder.set("content", content);
		builder.set("title", title);
		builder.set("date", Timestamp.of(date));

		System.out.println(this.authorEmail + this.content + this.title );

		getDatastore().put(builder.build());
	}

//	private IncompleteKey makeIncompleteKey() {
//		return Key.newBuilder(Mpost.getKey(), "title").build();
//	}

	public List<Mpost> getMposts() {
		// This query requires the index defined in index.yaml to work because of the
		// orderBy on date.

		EntityQuery query = Query.newEntityQueryBuilder()
								.setKind("Mpost")
								.setOrderBy(desc("date"))
								.build();

		QueryResults<Entity> results = getDatastore().run(query);

		ImmutableList.Builder<Mpost> resultListBuilder = ImmutableList.builder();

		while (results.hasNext()) {
			resultListBuilder.add(new Mpost(results.next()));
		}

		return resultListBuilder.build();
	}

	public static Mpost getMpost(String title, String author) {
		
		key = keyFactory.newKey(hashCode(title, author));

		EntityQuery query = Query.newEntityQueryBuilder()
				.setKind("Mpost")
				.setFilter(PropertyFilter.eq("__key__", key))
				.build();

		QueryResults<Entity> result = getDatastore().run(query);

		if (result.hasNext()) {
			return (new Mpost(result.next()));
		}
		return null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Mpost post = (Mpost) o;
		return Objects.equals(key, post.key) && Objects.equals(authorEmail, post.authorEmail)
				&& Objects.equals(title, post.title) && Objects.equals(content, post.content)
				&& Objects.equals(date, post.date);
	}

	@Override
	public int hashCode() {
		return Objects.hash(authorEmail, title);
	}
	
	
	public static int hashCode(String title, String authorEmail) {
		return Objects.hash(authorEmail, title);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("key", key).add("authorEmail", authorEmail).add("title", title)
				.add("content", content).add("date", date).toString();
	}
}
