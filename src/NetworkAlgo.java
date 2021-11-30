public interface NetworkAlgo {

    /**
     * Main function that executes the algorithm.
     *
     * @return Output
     */
    String RunAlgo() throws Exception;

    /**
     * Resets variable attributes to their default values after running a query.
     */
    void ResetAttributes();

}
