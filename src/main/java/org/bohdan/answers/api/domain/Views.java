package org.bohdan.answers.api.domain;

public class Views {

    public interface Id {}

    public interface IdName extends Id {}

    public interface FullErrorDescription extends IdName {}

    public interface FullErrorFields extends IdName {}

    public interface FullProfile extends IdName {}
}
