/*
 * Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.echo;

import com.google.api.server.spi.auth.EspAuthenticator;
import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiIssuer;
import com.google.api.server.spi.config.ApiIssuerAudience;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.Nullable;
import com.google.api.server.spi.response.UnauthorizedException;

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
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import com.google.common.collect.ImmutableList;
/**
 * The Echo API which Endpoints will be exposing.
 */
// [START echo_api_annotation]
@Api(
    name = "echo",
    version = "v1",
    namespace =
    @ApiNamespace(
        ownerDomain = "echo.example.com",
        ownerName = "echo.example.com",
        packagePath = ""
    ),
    // [START_EXCLUDE]
    issuers = {
        @ApiIssuer(
            name = "firebase",
            issuer = "https://securetoken.google.com/YOUR-PROJECT-ID",
            jwksUri =
                "https://www.googleapis.com/service_accounts/v1/metadata/x509/securetoken@system"
                    + ".gserviceaccount.com"
        )
    }
// [END_EXCLUDE]
)
// [END echo_api_annotation]

public class Echo {

    @ApiMethod(
      httpMethod = ApiMethod.HttpMethod.GET,
      name = "getPost",
      path = "getPost",
      authenticators = {EspAuthenticator.class},
      audiences = {"YOUR_OAUTH_CLIENT_ID"},
      clientIds = {"YOUR_OAUTH_CLIENT_ID"}
  )
    
  public PostMessage getPost(PostMessage message, @Named("title") @Nullable String title, @Named("author" ) @Nullable String author) 
  {

    return (returnPostContent (message, title, author));
  }

  private PostMessage returnPostContent (PostMessage message, String title, String author)
  {
    Mpost vpost = Mpost.getMpost(title, author);
    message.setContent(vpost.getContent());
    message.setDate(vpost.getDate());
    message.setTitle(vpost.getTitle());
    message.setAuthorEmail(vpost.getAuthorEmail());
    return(message);
  }


 @ApiMethod(
      httpMethod = ApiMethod.HttpMethod.GET,
      name = "getPosts",
      path = "getPosts",
      authenticators = {EspAuthenticator.class},
      audiences = {"YOUR_OAUTH_CLIENT_ID"},
      clientIds = {"YOUR_OAUTH_CLIENT_ID"}
  )
    
  public List<PostMessage> getPosts(PostMessage message) 
  {

    return (returnPostContents ());
  }

  private List<PostMessage> returnPostContents ()
  {
    Mpost post = new Mpost();
    List<Mpost> posts = post.getMposts();
    ImmutableList.Builder<PostMessage> resultListBuilder = ImmutableList.builder();

      for (Mpost vpost : posts) {

          PostMessage v = new PostMessage();
          v.setTitle(vpost.getTitle());
          v.setAuthorEmail(vpost.getAuthorEmail());
          v.setContent(vpost.getContent());
          v.setDate(vpost.getDate());
          resultListBuilder.add(v);
          }  

    return(resultListBuilder.build());
  }

@ApiMethod(
      httpMethod = ApiMethod.HttpMethod.POST,
      name = "createPost",
      path = "createPost",
      authenticators = {EspAuthenticator.class},
      audiences = {"YOUR_OAUTH_CLIENT_ID"},
      clientIds = {"YOUR_OAUTH_CLIENT_ID"}
  )
    
  public Message createPost(Message message, @Named("title") @Nullable String title
    , @Named("author" ) @Nullable String author
    , @Named("content") @Nullable String content) 
  {

    return (createMpost (message, title, author, content));
  }

  private Message createMpost (Message message, String title, String author, String content)
  {
    Mpost mpost = new Mpost(title, content, author);
    mpost.save();
    message.setMessage("201");
    return (message);
  }

@ApiMethod(
      httpMethod = ApiMethod.HttpMethod.PUT,
      name = "updatePost",
      path = "updatePost",
      authenticators = {EspAuthenticator.class},
      audiences = {"YOUR_OAUTH_CLIENT_ID"},
      clientIds = {"YOUR_OAUTH_CLIENT_ID"}
  )
    
  public Message updatePost(Message message, @Named("title") @Nullable String title
    , @Named("author" ) @Nullable String author
    , @Named("content") @Nullable String content) 
  {

    return (updateMpost (message, title, author, content));
  }

  private Message updateMpost (Message message, String title, String author, String content)
  {
    Mpost mpost = new Mpost(title, content, author);
    mpost.save();    
    message.setMessage("202");
    return (message);
  }  


  @ApiMethod(
      name = "getUserEmail",
      path = "getUserEmail",
      httpMethod = ApiMethod.HttpMethod.GET,
      authenticators = {EspAuthenticator.class},
      audiences = {"YOUR_OAUTH_CLIENT_ID"},
      clientIds = {"YOUR_OAUTH_CLIENT_ID"}
  )
  public Email getUserEmail(User user) throws UnauthorizedException {
    if (user == null) {
      throw new UnauthorizedException("Invalid credentials");
    }

    System.out.println("Here in the call");
    Email response = new Email();
    response.setEmail(user.getEmail());
    return response;
  }
  // [END google_id_token_auth]

 }
