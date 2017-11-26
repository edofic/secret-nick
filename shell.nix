{ pkgs ? import <nixpkgs> {} }:

pkgs.stdenv.mkDerivation rec {
  name = "secret-nick-2016";
  src = ./.;
  buildInputs = [
    pkgs.sbt
  ];

  shellHook = ''
    source DEV_SETTINGS
  '';
}
