/*
 * Copyright 2014 Yubico.
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package com.yubico.u2f.data.messages;

import com.google.common.base.Objects;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.yubico.u2f.data.messages.json.JsonSerializable;
import com.yubico.u2f.data.messages.json.Persistable;
import com.yubico.u2f.exceptions.U2fBadInputException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class RegisterResponse extends JsonSerializable implements Persistable {
    private static final int MAX_SIZE = 20000;

    /**
     * base64(raw registration response message)
     */
    private final String registrationData;

    /**
     * base64(UTF8(client data))
     */
    private final String clientData;

    private transient ClientData clientDataRef;

    public RegisterResponse(String registrationData, String clientData) {
        this.registrationData = checkNotNull(registrationData);
        this.clientData = checkNotNull(clientData);
        this.clientDataRef = new ClientData(clientData);
    }

    public String getRegistrationData() {
        return registrationData;
    }

    public ClientData getClientData() {
        return clientDataRef;
    }

    public String getRequestId() {
        return getClientData().getChallenge();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(registrationData, clientData);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RegisterResponse))
            return false;
        RegisterResponse other = (RegisterResponse) obj;
        return Objects.equal(clientData, other.clientData)
                && Objects.equal(registrationData, other.registrationData);
    }

    public static RegisterResponse fromJson(String json) throws U2fBadInputException {
        checkArgument(json.length() < MAX_SIZE, "Client response bigger than allowed");
        JsonObject data = new JsonParser().parse(json).getAsJsonObject();
        return new RegisterResponse(data.get("registrationData").getAsString(), data.get("clientData").getAsString());
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        clientDataRef = new ClientData(clientData);
    }
}
