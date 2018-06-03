package com.aigamelabs.mcts

/**
 * An enumeration of the types of nodes that can appear in an MCTS tree. The tree has three types of nodes. Terminal
 * nodes indicate that the state they contain is final, and they cannot be descended any further. All other nodes are
 * player nodes if they represent a player action, or stochastic nodes if they represent random outcomes (i.e.,
 * stochasticity in the environment).
 */
enum class NodeType {

    /**
     * A player node is a node where a player, be it a human or an AI, makes a decision. After a node of this type is
     * visited enough times (set in [Manager.uctNodeCreateThreshold]), all children nodes are create, one per possible
     * action.
     */
    PLAYER_NODE,

    /**
     * A stochastic node simulates randomness in the game. In stochastic nodes, the children nodes are not all created
     * at the same time. Whenever a stochastic node is descended, the action of the node is applied to the parent node
     * state. This generates a next state that is randomly sampled from the distribution dictated by the game mechanics.
     * The sampling is done in [TreeNode.sampleChild].
     */
    STOCHASTIC_NODE,

    /**
     * A terminal node does not represent any kind of action, be it a player decision or a stochastic transition. A
     * terminal node only contains a state and cannot be descended.
     */
    TERMINAL_NODE
}
