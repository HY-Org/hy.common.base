package org.hy.common.junit;


public class Child extends Father
{
    
    public Child()
    {
        super();
    }
    
    
    @Override
    public void hello()
    {
        System.out.println("Child Hello");
    }
    
}
