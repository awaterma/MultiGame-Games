package mx.ecosur.multigame.manantiales.token
{
    import mx.controls.Image;
    import mx.ecosur.multigame.enum.Color;
    import mx.ecosur.multigame.manantiales.enum.TokenType;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

public class ForestToken extends ManantialesToken
    {
        public function ForestToken () {
            super ();
            _tooltip = resourceManager.getString("StringsBundle", "manantiales.token.forest");
            _label = "F";
            _type = TokenType.FOREST;
            }
        }
}