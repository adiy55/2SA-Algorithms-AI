public interface NetworkAlgo {

    /**
     * Main function that executes the algorithm.
     *
     * @return Output
     */
    String RunAlgo() throws Exception;

    /**
     * Reset variable attributes to their default values after each query.
     */
    void ResetAttributes();

}
