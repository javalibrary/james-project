/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/

package org.apache.james.mailbox.inmemory;

import java.util.EnumSet;

import javax.inject.Inject;

import org.apache.james.mailbox.MailboxPathLocker;
import org.apache.james.mailbox.MailboxSession;
import org.apache.james.mailbox.model.MessageId;
import org.apache.james.mailbox.store.MailboxManagerConfiguration;
import org.apache.james.mailbox.store.MailboxSessionMapperFactory;
import org.apache.james.mailbox.store.SessionProvider;
import org.apache.james.mailbox.store.StoreMailboxAnnotationManager;
import org.apache.james.mailbox.store.StoreMailboxManager;
import org.apache.james.mailbox.store.StoreMessageManager;
import org.apache.james.mailbox.store.StoreRightManager;
import org.apache.james.mailbox.store.event.DelegatingMailboxListener;
import org.apache.james.mailbox.store.mail.model.Mailbox;
import org.apache.james.mailbox.store.mail.model.impl.MessageParser;
import org.apache.james.mailbox.store.quota.QuotaComponents;
import org.apache.james.mailbox.store.search.MessageSearchIndex;

public class InMemoryMailboxManager extends StoreMailboxManager {

    public static final EnumSet<MailboxCapabilities> MAILBOX_CAPABILITIES = EnumSet.of(MailboxCapabilities.Move,
        MailboxCapabilities.UserFlag,
        MailboxCapabilities.Namespace,
        MailboxCapabilities.Annotation,
        MailboxCapabilities.ACL,
        MailboxCapabilities.Quota);
    public static final EnumSet<MessageCapabilities> MESSAGE_CAPABILITIES = EnumSet.of(MessageCapabilities.UniqueID);

    @Inject
    public InMemoryMailboxManager(MailboxSessionMapperFactory mailboxSessionMapperFactory, SessionProvider sessionProvider,
                                  MailboxPathLocker locker, MessageParser messageParser, MessageId.Factory messageIdFactory,
                                  DelegatingMailboxListener delegatingMailboxListener,
                                  StoreMailboxAnnotationManager annotationManager,
                                  StoreRightManager storeRightManager,
                                  QuotaComponents quotaComponents,
                                  MessageSearchIndex searchIndex) {
        super(mailboxSessionMapperFactory, sessionProvider, locker, messageParser, messageIdFactory,
            annotationManager, delegatingMailboxListener, storeRightManager, quotaComponents, searchIndex, MailboxManagerConfiguration.DEFAULT);
    }

    @Override
    public EnumSet<MailboxCapabilities> getSupportedMailboxCapabilities() {
        return MAILBOX_CAPABILITIES;
    }
    
    @Override
    public EnumSet<MessageCapabilities> getSupportedMessageCapabilities() {
        return MESSAGE_CAPABILITIES;
    }

    @Override
    protected StoreMessageManager createMessageManager(Mailbox mailbox, MailboxSession session) {
        return new InMemoryMessageManager(getMapperFactory(),
            getMessageSearchIndex(),
            getDelegationListener(),
            getLocker(),
            mailbox,
            getQuotaComponents().getQuotaManager(),
            getQuotaComponents().getQuotaRootResolver(),
            getMessageParser(),
            getMessageIdFactory(),
            configuration.getBatchSizes(),
            getStoreRightManager());
    }
}
