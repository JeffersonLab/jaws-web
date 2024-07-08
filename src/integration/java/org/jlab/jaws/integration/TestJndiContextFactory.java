package org.jlab.jaws.integration;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import javax.naming.*;
import javax.naming.spi.InitialContextFactory;

/**
 * Support class for testing with JNDI.
 *
 * @author slominskir
 */
public class TestJndiContextFactory implements InitialContextFactory {

  private static final MemoryContext INIT_CTX = new MemoryContext();

  static {
    try {
      INIT_CTX.rebind("java:comp/env", new MemoryContext());
    } catch (Exception ex) {
      throw new ExceptionInInitializerError(ex);
    }
  }

  public TestJndiContextFactory() {
    System.setProperty(
        Context.INITIAL_CONTEXT_FACTORY, "org.jlab.jaws.integration.TestJndiContextFactory");
  }

  @Override
  public Context getInitialContext(Hashtable<?, ?> environment) {
    return INIT_CTX;
  }

  private static class MemoryContext implements Context {

    private static final Map<String, Object> DB = new HashMap<>();

    @Override
    public Object lookup(Name name) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object lookup(String name) {
      return DB.get(name);
    }

    @Override
    public void bind(Name name, Object obj) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void bind(String name, Object obj) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void rebind(Name name, Object obj) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void rebind(String name, Object obj) {
      DB.put(name, obj);
    }

    @Override
    public void unbind(Name name) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void unbind(String name) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void rename(Name oldName, Name newName) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void rename(String oldName, String newName) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public NamingEnumeration<NameClassPair> list(Name name) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public NamingEnumeration<NameClassPair> list(String name) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public NamingEnumeration<Binding> listBindings(Name name) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public NamingEnumeration<Binding> listBindings(String name) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void destroySubcontext(Name name) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void destroySubcontext(String name) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Context createSubcontext(Name name) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Context createSubcontext(String name) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object lookupLink(Name name) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object lookupLink(String name) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public NameParser getNameParser(Name name) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public NameParser getNameParser(String name) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Name composeName(Name name, Name prefix) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String composeName(String name, String prefix) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object addToEnvironment(String propName, Object propVal) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object removeFromEnvironment(String propName) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Hashtable<?, ?> getEnvironment() {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void close() {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getNameInNamespace() {
      throw new UnsupportedOperationException("Not supported yet.");
    }
  }
}
