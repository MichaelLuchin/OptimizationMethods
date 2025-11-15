package util.functionalInterfaces;
import util.DoubleVector;

@FunctionalInterface
public interface IFunctionND{
    double call(final DoubleVector arg);
}
