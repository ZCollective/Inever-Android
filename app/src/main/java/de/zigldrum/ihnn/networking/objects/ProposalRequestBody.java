package de.zigldrum.ihnn.networking.objects;

public class ProposalRequestBody {

    final String string;
    final String sender;

    public ProposalRequestBody(String string, String sender) {
        this.string = string;
        this.sender = sender;
    }
}
